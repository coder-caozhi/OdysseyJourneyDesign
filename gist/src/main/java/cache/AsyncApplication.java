package cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 缓存模块启动编排器 —— 生产级质量.
 *
 * <h3>核心职责</h3>
 * <ol>
 *   <li><b>启动时全量加载</b> —— Spring 容器就绪后，并行调用所有 Loader 完成首次缓存预热</li>
 *   <li><b>定时增量刷新</b> —— 单例 ScheduledExecutorService 每隔 5 分钟触发一次，
 *       每个 Loader 通过比较 {@code latestUpdateTime} 决定是否需要更新本地缓存</li>
 *   <li><b>健康状态暴露</b> —— 通过 {@link #isCacheInitSuccess()} 原子标记，
 *       供外部（K8s Readiness Probe、监控等）判断缓存模块是否就绪</li>
 * </ol>
 *
 * <h3>关键设计</h3>
 * <pre>
 * 启动流程：
 *   Spring容器就绪 → CommandLineRunner.run()
 *     → CompletableFuture 并行 load()
 *     → allOf().join()
 *     → cacheInitSuccess.set(true)
 *     → 启动定时刷新线程池
 *
 * 定时刷新：
 *   every 5min → forEach loader → loader.load()
 *     → fetchFromRemote() RPC调用
 *     → 比较 latestUpdateTime
 *     → 有更新则 CacheDataHolder.put()
 *     → 无更新则 skip (仅debug日志)
 * </pre>
 */
@Slf4j
@Component
@SpringBootApplication
public class AsyncApplication implements CommandLineRunner {

    // ====================== 依赖注入 ======================

    /** Spring 自动收集所有 AbstractCacheLoader 子类 Bean */
    @Autowired
    private List<AbstractCacheLoader<?>> cacheLoaders;

    // ====================== 状态标记 ======================

    /**
     * 服务初始化时缓存模块是否成功完成首次加载.
     *
     * <p>使用场景：
     * <ul>
     *   <li>K8s Readiness Probe 就绪探针：GET /actuator/health → 检查此标记</li>
     *   <li>运维监控大盘：暴露此标记为 metrics</li>
     *   <li>上游调用方：调用前判断缓存是否就绪，未就绪则降级</li>
     * </ul>
     */
    private final AtomicBoolean cacheInitSuccess = new AtomicBoolean(false);

    // ====================== 定时刷新线程池 ======================

    /**
     * 单例定时线程池 —— 负责每 5 分钟触发一次缓存增量刷新.
     *
     * <p>为什么用单线程？
     * <ul>
     *   <li>调度线程只负责"触发"，实际 RPC 调用在每个 Loader 内部已经并行化</li>
     *   <li>避免多调度线程重复触发</li>
     *   <li>资源可控</li>
     * </ul>
     */
    private final ScheduledExecutorService refreshScheduler =
            Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "cache-refresh-" + counter.getAndIncrement());
                    t.setDaemon(true);       // 守护线程：主线程退出时自动终止
                    t.setUncaughtExceptionHandler((thread, ex) ->
                            log.error("Uncaught exception in cache refresh thread [{}]", thread.getName(), ex));
                    return t;
                }
            });

    // ====================== 生命周期 ======================

    public static void main(String[] args) {
        SpringApplication.run(AsyncApplication.class, args);
    }

    /**
     * Spring 容器完全启动后的回调 —— 执行首次缓存预热 + 启动定时刷新.
     */
    @Override
    public void run(String... args) {
        log.info("========================================");
        log.info("Cache module initializing... found {} loader(s)", cacheLoaders.size());
        log.info("========================================");

        // ---------- Phase 1: 启动时全量并行加载 ----------
        boolean initOk = doInitialLoad();

        // 设置原子标记
        cacheInitSuccess.set(initOk);
        if (initOk) {
            log.info("✅ Cache module INITIALIZED successfully");
        } else {
            log.error("❌ Cache module initialization had failures, check logs above");
        }

        // ---------- Phase 2: 启动定时增量刷新 ----------
        startScheduledRefresh();

        // 打印加载结果
        log.info(CacheDataHolder.snapshot());
    }

    /**
     * 应用关闭时优雅停止定时刷新线程池.
     */
    @PreDestroy
    public void shutdown() {
        log.info("Cache refresh scheduler shutting down...");
        refreshScheduler.shutdown();
        try {
            if (!refreshScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                refreshScheduler.shutdownNow();
                log.warn("Cache refresh scheduler forcibly shut down");
            }
        } catch (InterruptedException e) {
            refreshScheduler.shutdownNow();
            Thread.currentThread().interrupt();
            log.warn("Cache refresh scheduler shutdown interrupted");
        }
        log.info("Cache refresh scheduler stopped");
    }

    // ====================== 公开 API ======================

    /**
     * 供外部（健康检查、监控）查询缓存模块是否已成功初始化.
     */
    public boolean isCacheInitSuccess() {
        return cacheInitSuccess.get();
    }

    // ====================== 内部实现 ======================

    /**
     * 首次全量并行加载 —— 所有 Loader 并发执行.
     *
     * <p>单个 Loader 失败不影响其他 Loader.
     *
     * @return true 表示所有 Loader 均加载成功（即各自的 RPC 调用成功返回了数据）
     */
    private boolean doInitialLoad() {
        log.info("Phase 1/2: Starting initial parallel cache load ({} loaders)...", cacheLoaders.size());

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (AbstractCacheLoader<?> loader : cacheLoaders) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    boolean updated = loader.load();
                    if (updated) {
                        successCount.incrementAndGet();
                    } else {
                        // load() 返回 false 意味着 RPC 失败或远端无数据
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    log.error("Loader [{}] threw unexpected exception during init",
                            loader.getClass().getSimpleName(), e);
                }
            });
            futures.add(future);
        }

        // 等待所有 Loader 完成（不设超时，因为有 CompletableFuture 包装超时在 load() 内部）
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("Phase 1/2: Initial load completed in {}ms — "
                        + "success={}, fail={}, total={}",
                elapsed, successCount.get(), failCount.get(), cacheLoaders.size());

        return failCount.get() == 0;
    }

    /**
     * 启动定时增量刷新 —— 单例 ScheduledExecutorService.
     *
     * <p>调度策略：
     * <ul>
     *   <li>首次延迟：{@link CacheConstant#REFRESH_INITIAL_DELAY_SECONDS} 秒（给应用预热时间）</li>
     *   <li>执行间隔：{@link CacheConstant#REFRESH_INTERVAL_MINUTES} 分钟</li>
     *   <li>每次触发 → 遍历所有 Loader → 每个 Loader 自己比较 updateTime 决定是否更新</li>
     * </ul>
     */
    private void startScheduledRefresh() {
        log.info("Phase 2/2: Starting scheduled refresh — every {}min, initial delay {}s",
                CacheConstant.REFRESH_INTERVAL_MINUTES, CacheConstant.REFRESH_INITIAL_DELAY_SECONDS);

        refreshScheduler.scheduleAtFixedRate(
                this::scheduledRefreshTask,
                CacheConstant.REFRESH_INITIAL_DELAY_SECONDS,
                CacheConstant.REFRESH_INTERVAL_MINUTES,
                TimeUnit.MINUTES
        );

        log.info("Scheduled refresh started on thread pool [cache-refresh]");
    }

    /**
     * 定时刷新任务体 —— 每次被调度线程触发时执行.
     *
     * <p>遍历所有 Loader，串行调用（因为单个 Loader 的 RPC 已设超时，
     * 且对比 updateTime 是纯内存操作极快，没必要在此层并行化）.
     */
    private void scheduledRefreshTask() {
        log.debug("Scheduled refresh tick...");

        int updatedCount = 0;
        for (AbstractCacheLoader<?> loader : cacheLoaders) {
            try {
                if (loader.load()) {
                    updatedCount++;
                }
            } catch (Exception e) {
                log.error("Scheduled refresh failed for loader [{}]",
                        loader.getClass().getSimpleName(), e);
            }
        }

        if (updatedCount > 0) {
            log.info("Scheduled refresh: {} cache(s) updated", updatedCount);
        }
    }
}
