package org.george.threads;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产级线程池工具类
 * 完全线程安全、自定义命名、可直接上线
 */
public class ThreadPoolFactory {

    private ThreadPoolFactory() {}

    public static ThreadPoolExecutor getBusinessThreadPool() {
        // 正确：线程安全的自增序号
        AtomicInteger threadIndex = new AtomicInteger(1);

        // 线程工厂：生产标准写法
        ThreadFactory threadFactory = r -> {
            Thread thread = new Thread(r);
            // 关键：给线程起业务名！线上排查日志必备
            thread.setName("business-thread-" + threadIndex.getAndIncrement());
            // 建议设置守护线程
            thread.setDaemon(true);
            return thread;
        };

        // 7 个参数齐全！生产标准
        return new ThreadPoolExecutor(
                5,
                20,
                30L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                threadFactory,          // 自定义命名线程工厂（线程安全）
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
        );
    }
}