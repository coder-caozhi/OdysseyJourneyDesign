package org.george.threads;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolProductDemo {
    public static void main(String[] args) {
        // 获取生产级线程池
        ThreadPoolExecutor threadPool = ThreadPoolFactory.getBusinessThreadPool();

        // 模拟100条业务任务
        for (int i = 1; i <= 100; i++) {
            int taskId = i;
            // 提交无返回任务
            threadPool.execute(() -> {
                System.out.printf("【%s】正在处理任务：%d%n",
                        Thread.currentThread().getName(), taskId);
                try {
                    // 模拟业务耗时
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 优雅关闭
        threadPool.shutdown();
        try {
            // 等待所有任务执行完成，最多等1分钟
            if (!threadPool.awaitTermination(1, TimeUnit.MINUTES)) {
                // 超时还没执行完，强制关闭
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }
}