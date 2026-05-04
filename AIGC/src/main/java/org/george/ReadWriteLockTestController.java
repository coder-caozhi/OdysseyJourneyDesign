package org.george;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 并发测试：验证读写锁的互斥与共享行为
 */
@RestController
@RequestMapping("/api/rw")
public class ReadWriteLockTestController {

    private final ReadWriteLockCacheService cacheService;

    public ReadWriteLockTestController(ReadWriteLockCacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * 测试1：并发读 —— 验证读读不互斥
     *
     * GET /api/rw/test-read?id=1001
     *
     * 预期：10 个读线程几乎同时执行，不会互相阻塞
     */
    @GetMapping("/test-read")
    public Map<String, Object> testConcurrentRead(@RequestParam String id) throws Exception {

        int threadCount = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int seq = i + 1;
            pool.submit(() -> {
                try {
                    String result = cacheService.getData(id);
                    System.out.println("  [读线程-" + seq + "] 结果: " + result);
                } catch (Exception e) {
                    System.out.println("  [读线程-" + seq + "] 异常: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long elapsed = System.currentTimeMillis() - start;
        pool.shutdown();

        return Map.of(
            "test", "并发读测试",
            "threads", threadCount,
            "elapsedMs", elapsed,
            "expected", "读读不互斥，应几乎同时完成"
        );
    }

    /**
     * 测试2：读写并发 —— 验证读写互斥
     *
     * GET /api/rw/test-read-write?id=1001
     *
     * 预期：
     *   - 读线程和写线程不会同时执行
     *   - 写操作期间所有读操作会阻塞等待
     */
    @GetMapping("/test-read-write")
    public Map<String, Object> testReadWrite(@RequestParam String id) throws Exception {

        ExecutorService pool = Executors.newFixedThreadPool(6);
        CountDownLatch latch = new CountDownLatch(6);

        long start = System.currentTimeMillis();

        // 启动 4 个读线程
        for (int i = 1; i <= 4; i++) {
            final int seq = i;
            pool.submit(() -> {
                try {
                    System.out.println("[读-" + seq + "] 开始");
                    String result = cacheService.getData(id);
                    System.out.println("[读-" + seq + "] 完成: " + result);
                } catch (Exception e) {
                    System.out.println("[读-" + seq + "] 异常: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // 启动 1 个写线程（稍后启动，模拟并发场景）
        pool.submit(() -> {
            try {
                Thread.sleep(50); // 让读线程先启动
                System.out.println("[写] 开始更新...");
                cacheService.updateData(id, "{\"id\":\"" + id + "\",\"name\":\"新数据\",\"price\":199.9}");
                System.out.println("[写] 更新完成");
            } catch (Exception e) {
                System.out.println("[写] 异常: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        // 再启动 1 个读线程（写操作之后）
        pool.submit(() -> {
            try {
                Thread.sleep(300); // 等写操作进行中
                System.out.println("[读-延迟] 开始");
                String result = cacheService.getData(id);
                System.out.println("[读-延迟] 完成: " + result);
            } catch (Exception e) {
                System.out.println("[读-延迟] 异常: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        long elapsed = System.currentTimeMillis() - start;
        pool.shutdown();

        return Map.of(
            "test", "读写互斥测试",
            "elapsedMs", elapsed,
            "expected", "写操作期间读操作应被阻塞"
        );
    }
}
