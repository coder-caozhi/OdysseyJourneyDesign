package org.george.threads.pool;

import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

@Component
public class CustomForkJoinPoolExecutor {

    // ====================== 核心：自定义 ForkJoin 线程池 ======================
    private final ForkJoinPool customForkJoinPool;

    public CustomForkJoinPoolExecutor() {
        // 1. CPU 核心数
        int parallelism = Runtime.getRuntime().availableProcessors();

        // 2. 自定义线程工厂（解决你之前的报错！）
        ForkJoinPool.ForkJoinWorkerThreadFactory threadFactory = pool -> {
            // 必须用内部子类，不能直接 new ForkJoinWorkerThread
            return new MyWorkerThread(pool);
        };

        // 3. 创建自定义 ForkJoinPool
        this.customForkJoinPool = new ForkJoinPool(
                parallelism,    // 你要的第一个参数
                threadFactory,  // 你要的自定义线程工厂
                null,
                false
        );
    }

    // ====================== 解决报错的关键：自定义内部线程类 ======================
    private static class MyWorkerThread extends ForkJoinWorkerThread {
        protected MyWorkerThread(ForkJoinPool pool) {
            super(pool);
            // 在这里自定义线程属性
            setName("custom-async-forkjoin-thread-" + getPoolIndex());
            setDaemon(true);
            setContextClassLoader(getClass().getClassLoader());
        }
    }

    // ====================== 你要的异步执行方法 ======================
    public void runAsyncTask(Runnable task) {
        // 重点：把自定义 forkJoinPool 传入
        CompletableFuture.runAsync(task, customForkJoinPool);
    }

    // 提供线程池给外部使用
    public ForkJoinPool getForkJoinPool() {
        return customForkJoinPool;
    }
}