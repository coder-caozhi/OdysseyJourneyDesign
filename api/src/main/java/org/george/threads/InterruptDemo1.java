package org.george.threads;

public class InterruptDemo1 {
    public static void main(String[] args) throws InterruptedException {
        Thread t2 = new Thread(() -> {
            // 一直循环干活
            while (true) {
                System.out.println("t2 正在正常运行中...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // 被中断会进到这里
                    System.out.println("t2 收到中断唤醒了，但我偏不退出，继续跑");
                }
            }
        }, "t2");

        t2.start();
        // 主线程等3秒
        Thread.sleep(3000);

        System.out.println("==== 主线程调用 t2.interrupt() ====");
        t2.interrupt();

        // 再等2秒看状态
        Thread.sleep(2000);
        System.out.println("t2 线程状态：" + t2.getState());
    }
}