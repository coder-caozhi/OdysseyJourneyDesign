package cache;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 缓存加载器抽象基类 —— 模板方法模式的核心.
 *
 * <p>每个子类对应一个管理服务的 RPC 接口，完成：
 * <ol>
 *   <li>通过 RPC 调用管理服务，拿到 {@link CacheData}（全量数据 + 最新更新时间）</li>
 *   <li>与本地缓存的 latestUpdateTime 比较</li>
 *   <li>如果远端更新 → 全量替换本地缓存</li>
 *   <li>如果未更新 → 跳过，打印 debug 日志</li>
 * </ol>
 *
 * <p>子类只需实现两个方法：
 * <ul>
 *   <li>{@link #getCacheKey()}      —— 返回缓存 Key（见 {@link CacheConstant}）</li>
 *   <li>{@link #fetchFromRemote()}  —— 实际的 RPC 调用逻辑</li>
 * </ul>
 *
 * @param <T> 业务实体类型
 */
@Slf4j
public abstract class AbstractCacheLoader<T> {

    // ====================== 模板方法 ======================

    /**
     * 加载/刷新缓存.
     *
     * <p>模板流程：
     * <pre>
     * 1. fetchFromRemote()   →  RPC 调用管理服务
     * 2. 比较 latestUpdateTime
     * 3. 有更新 → CacheDataHolder.put()
     * 4. 无更新 → skip
     * </pre>
     *
     * @return true 表示本次实际刷新了数据，false 表示数据未变化、跳过
     */
    public final boolean load() {
        String cacheKey = getCacheKey();
        log.debug("CacheLoader [{}] starting refresh...", cacheKey);

        CacheData<T> remoteData;
        try {
            remoteData = fetchWithTimeout();
        } catch (TimeoutException e) {
            log.error("CacheLoader [{}] RPC timed out after {}s", cacheKey,
                    CacheConstant.RPC_TIMEOUT_SECONDS, e);
            return false;
        } catch (Exception e) {
            log.error("CacheLoader [{}] RPC call failed", cacheKey, e);
            return false;
        }

        // 远端返回空数据（可能是接口异常），不更新本地
        if (remoteData == null || remoteData.isEmpty()) {
            log.warn("CacheLoader [{}] remote returned empty data, skip update", cacheKey);
            return false;
        }

        // 判断是否需要更新
        LocalDateTime localTime = CacheDataHolder.getLatestUpdateTime(cacheKey);
        LocalDateTime remoteTime = remoteData.getLatestUpdateTime();

        if (localTime == null) {
            // 首次加载
            CacheDataHolder.put(cacheKey, remoteData);
            log.info("CacheLoader [{}] first load completed, {} records, latestTime={}",
                    cacheKey, remoteData.getDataList().size(), remoteTime);
            return true;
        }

        if (remoteTime != null && remoteTime.isAfter(localTime)) {
            // 远端有更新
            CacheDataHolder.put(cacheKey, remoteData);
            log.info("CacheLoader [{}] data changed, refreshed {} records, "
                            + "localTime={} → remoteTime={}",
                    cacheKey, remoteData.getDataList().size(), localTime, remoteTime);
            return true;
        }

        // 数据未变化
        log.debug("CacheLoader [{}] data unchanged (latestTime={}), skip", cacheKey, localTime);
        return false;
    }

    // ====================== 内部工具方法 ======================

    /**
     * 带超时的 RPC 调用包装.
     */
    private CacheData<T> fetchWithTimeout() throws Exception {
        return CompletableFuture
                .supplyAsync(this::fetchFromRemote)
                .get(CacheConstant.RPC_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    // ====================== 子类必须实现 ======================

    /**
     * 返回当前 Loader 对应的缓存 Key.
     */
    public abstract String getCacheKey();

    /**
     * 通过 RPC 调用管理服务获取最新数据.
     *
     * <p>管理服务接口返回 {@link CacheData}：
     * <ul>
     *   <li>{@code dataList} —— 该表全部记录</li>
     *   <li>{@code latestUpdateTime} —— 全部记录中最新的 update_time</li>
     * </ul>
     *
     * @return 远端最新数据
     */
    protected abstract CacheData<T> fetchFromRemote();
}
