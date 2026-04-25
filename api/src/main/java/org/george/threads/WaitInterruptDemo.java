package org.george.threads;

public class WaitInterruptDemo {
    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();

        Thread t = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("线程进入 wait() 无限等待...");
                    // 无限等待 WAITING 状态
                    lock.wait();
                } catch (InterruptedException e) {
                    System.out.println("线程 wait() 被中断唤醒，抛异常");
                }
            }
        });

        t.start();
        Thread.sleep(2000);

        System.out.println("主线程调用 interrupt()");
        t.interrupt();
    }
}