package org.george.threads;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockInterruptDemo {
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {

        // 线程1：先占用锁不释放
        Thread t1 = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("线程1 拿到ReentrantLock，一直持有不释放");
                while (true) {}
            } finally {
                lock.unlock();
            }
        });

        // 线程2：用 lockInterruptibly() 尝试抢锁
        Thread t2 = new Thread(() -> {
            try {
                System.out.println("线程2 开始用 lockInterruptibly() 抢锁...");
                // 关键点：可中断的抢锁
                lock.lockInterruptibly();
                lock.unlock();
            } catch (InterruptedException e) {
                // 被中断后直接进到这里
                System.out.println("线程2 抢锁过程被中断了！抛出 InterruptedException");
            }
        });

        t1.start();
        Thread.sleep(500);
        t2.start();

        // 等2秒，让t2进入 等待锁 的阻塞状态
        Thread.sleep(2000);
        System.out.println("主线程：调用 t2.interrupt()");
        // 中断正在等锁的线程
        t2.interrupt();

        Thread.sleep(500);
        System.out.println("t2 线程当前状态：" + t2.getState());
        System.out.println("结论：ReentrantLock#lockInterruptibly() 抢锁可以被中断！");
    }
}