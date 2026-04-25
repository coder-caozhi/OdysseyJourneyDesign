package org.george.threads;

public class ThreadLifecycleDemo {

    // 共享资源：仓库，容量为 1
    static class Warehouse {
        private boolean hasProduct = false;

        // 生产方法
        public synchronized void produce() throws InterruptedException {
            // 如果仓库有货，生产者需要等待（进入 WAITING 状态，释放锁）
            while (hasProduct) {
                System.out.println("🏭 生产者: 仓库满了，等待消费者消费... (调用 wait)");
                this.wait(); 
            }

            // 模拟生产耗时（进入 TIMED_WAITING 状态，但不释放锁！）
            // 注意：这里 sleep 是为了演示状态，实际生产中应避免在持有锁时长时间 sleep
            Thread.sleep(1500); 
            
            hasProduct = true;
            System.out.println("🏭 生产者: 生产了一个产品，通知消费者 (调用 notifyAll)");
            this.notifyAll();
        }

        // 消费方法
        public synchronized void consume() throws InterruptedException {
            // 如果仓库没货，消费者需要等待（进入 WAITING 状态，释放锁）
            while (!hasProduct) {
                System.out.println("🛒 消费者: 仓库空了，等待生产者生产... (调用 wait)");
                this.wait();
            }

            // 模拟消费耗时
            Thread.sleep(1500);

            hasProduct = false;
            System.out.println("🛒 消费者: 消费了产品，通知生产者 (调用 notifyAll)");
            this.notifyAll();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Warehouse warehouse = new Warehouse();

        // 1. 创建生产者线程
        Thread producer = new Thread(() -> {
            try {
                while (true) warehouse.produce();
            } catch (InterruptedException e) { e.printStackTrace(); }
        }, "Producer-Thread");

        // 2. 创建消费者线程
        Thread consumer = new Thread(() -> {
            try {
                while (true) warehouse.consume();
            } catch (InterruptedException e) { e.printStackTrace(); }
        }, "Consumer-Thread");

        // 3. 【关键】创建监控线程，用于观察生命周期状态
        Thread monitor = new Thread(() -> {
            System.out.println("⏰ 监控线程启动，每 500ms 打印一次线程状态...");
            while (true) {
                try {
                    Thread.sleep(500);
                    // 打印状态：线程名 -> 状态
                    System.out.printf("👀 [监控] %-18s 状态: %s%n", producer.getName(), producer.getState());
                    System.out.printf("👀 [监控] %-18s 状态: %s%n", consumer.getName(), consumer.getState());
                    System.out.println("---------------------------------------------------");
                } catch (InterruptedException e) { break; }
            }
        }, "Monitor-Thread");

        // 启动所有线程
        producer.start();
        consumer.start();
        monitor.start();
        
        // 主线程运行 10 秒后退出，防止死循环
        Thread.sleep(10000);
        System.exit(0);
    }
}