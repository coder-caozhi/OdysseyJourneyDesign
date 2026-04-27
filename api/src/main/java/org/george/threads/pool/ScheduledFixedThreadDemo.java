package org.george.threads.pool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledFixedThreadDemo {
    public static void main(String[] args) {
        // 创建【固定3个线程】的定时任务线程池
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);

        // 任务1：延迟 1秒后，只执行一次
        pool.schedule(() -> {
            System.out.println("延迟任务执行：" + Thread.currentThread().getName());
        }, 1, TimeUnit.SECONDS);


        // 任务2：延迟0秒启动，每隔2秒 循环执行一次
        pool.scheduleAtFixedRate(() -> {
            System.out.println("周期定时任务：" + Thread.currentThread().getName());
        }, 0, 2, TimeUnit.SECONDS);


        // 不用的时候关闭（测试可以注释，看定时一直跑）
        // pool.shutdown();
    }
}