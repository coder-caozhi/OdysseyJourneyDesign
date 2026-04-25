package org.george.threads;

/**
 * 线程 高频 API 大全（工作/面试必背）
 * 1. currentThread()   → 获取当前正在执行的线程对象
 * 2. getName()         → 获取线程名字
 * 3. setName()         → 设置线程名字
 * 4. start()           → 启动线程（必须用）
 * 5. sleep(long)       → 线程休眠（单位：毫秒）
 * 6. join()            → 等待该线程执行完再继续
 * 7. isAlive()         → 判断线程是否还活着
 * 8. setPriority(int)  → 设置线程优先级（1-10）
 * 9. interrupt()       → 中断线程休眠/等待
 * 10. yield()          → 让出CPU执行权
 */
public class ThreadAPIDemo {
    public static void main(String[] args) throws InterruptedException {

        // ==========================================
        // 1. 获取当前线程 + 线程名字（最最常用！）
        // ==========================================
        Thread mainThread = Thread.currentThread();
        System.out.println("当前线程：" + mainThread.getName()); // main
        System.out.println("主线程 ID：" + mainThread.getId());
        System.out.println("主线程优先级：" + mainThread.getPriority() + "\n");


        // ==========================================
        // 2. 创建线程 + 设置名字 + 启动
        // ==========================================
        Thread t1 = new Thread(() -> {
            // 子线程内部：获取自己的名字
            String name = Thread.currentThread().getName();
            System.out.println(name + " 开始运行");

            try {
                // ==========================================
                // 3. sleep：休眠 1 秒（暂停执行，不释放锁）
                // ==========================================
                System.out.println(name + " 开始休眠 1 秒");
                Thread.sleep(1000);
                System.out.println(name + " 休眠结束");

            } catch (InterruptedException e) {
                System.out.println(name + " 被中断了！");
                return;
            }

            System.out.println(name + " 执行完毕");
        });

        // 设置线程名字
        t1.setName("工作线程-1");
        // 设置优先级（1最低 5默认 10最高）
        t1.setPriority(Thread.NORM_PRIORITY);

        // ==========================================
        // 4. start()：启动线程（千万不要用 run()！）
        // ==========================================
        t1.start();

        // ==========================================
        // 5. isAlive()：判断线程是否还在运行
        // ==========================================
        System.out.println(t1.getName() + " 是否存活：" + t1.isAlive() + "\n");


        // ==========================================
        // 6. join()：主线程等待 t1 执行完再继续
        // ==========================================
        System.out.println("主线程等待 t1 执行完毕...");
        t1.join();
        System.out.println(t1.getName() + " 是否存活：" + t1.isAlive());


        // ==========================================
        // 7. interrupt()：中断休眠中的线程
        // ==========================================
        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(5000); // 睡 5 秒
            } catch (InterruptedException e) {
                System.out.println("\nt2 被 interrupt 中断休眠！");
            }
        });
        t2.setName("休眠线程");
        t2.start();

        // 主线程 1 秒后中断 t2
        Thread.sleep(1000);
        t2.interrupt();


        // ==========================================
        // 8. yield()：让出CPU，给其他线程执行机会
        // ==========================================
        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("t3 执行：" + i);
                Thread.yield(); // 让出CPU
            }
        }, "让出CPU线程"); // 直接在构造方法里设置线程名字
        t3.start();
    }
}