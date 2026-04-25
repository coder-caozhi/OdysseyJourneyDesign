package org.george.threads;

import java.util.concurrent.*;

public class ThreadPoolDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1. 创建线程池（最标准写法）
        // 参数：核心线程数、最大线程数、空闲时间、时间单位、任务队列
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                3,          // 核心线程：一直存活的线程数
                10,         // 最大线程：最多同时跑的线程数
                1,         // 空闲线程存活时间
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5)  // 任务排队队列
        );

        // 2. 提交 10 个任务给线程池执行
        for (int i = 1; i <= 10; i++) {
            int taskNum = i;
            // 提交任务（Lambda 写法）
            threadPool.execute(() -> {
                System.out.println("线程：" + Thread.currentThread().getName()
                        + " 正在执行任务 " + taskNum);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            // 需要结果，用 submit() 提交 Callable 任务
//            Future<String> future = threadPool.submit( () -> {
//                System.out.println("线程：" + Thread.currentThread().getName()
//                        + " 正在执行 Callable 任务 " + taskNum);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return "Callable 任务 " + taskNum + " 执行完毕";
//            });
//            String result = future.get();
//            System.out.println("获取 Callable 任务 " + taskNum + " 的结果: " + result);
        }

        // 3. 关闭线程池
        // 3.1 shutdown()：平滑关闭，等待已提交的任务执行完毕后关闭线程池
        threadPool.shutdown();
        // 3.2 shutdownNow()：立即关闭，尝试中断正在执行的任务，并返回未执行的任务列表
//        List<Runnable> notExecutedTasks = threadPool.shutdownNow();
//        System.out.println("未执行的任务数: " + notExecutedTasks.size());
//        System.out.println("未执行的任务列表: " + notExecutedTasks);

    }
}