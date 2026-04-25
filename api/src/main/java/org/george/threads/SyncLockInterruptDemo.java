package org.george.threads;

public class SyncLockInterruptDemo {

    public static void main(String[] args) throws InterruptedException {
        // 同一把锁对象
        Object lock = new Object();

        // 线程1：先抢占锁，并且一直持有不释放
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("线程1 拿到锁，一直持有不释放");
                while (true) {
                    // 一直占用锁，不退出代码块
                }
            }
        });

        // 线程2：抢同一个synchronized锁，会进入 BLOCKED
        Thread t2 = new Thread(() -> {
            System.out.println("线程2 开始抢synchronized锁...");
            synchronized (lock) {
                System.out.println("线程2 终于拿到锁了");
            }
            // 这句永远没机会执行
            System.out.println("线程2 执行结束");
        });

        t1.start();
        Thread.sleep(500);
        t2.start();

        // 主线程休眠2秒，让t2进入 抢锁阻塞状态
        Thread.sleep(2000);
        System.out.println("主线程：调用 t2.interrupt() 尝试中断");
        // 尝试中断正在BLOCKED抢锁的线程
        t2.interrupt();

        // 打印线程状态：依然是 BLOCKED，毫无变化
        System.out.println("t2 线程当前状态：" + t2.getState());
        System.out.println("结论：synchronized 抢锁阻塞，不响应中断！");
    }
}