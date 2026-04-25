package org.george.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Java 创建线程的 3 种标准方式
 * 1. 继承 Thread 类
 * 2. 实现 Runnable 接口（无返回值）
 * 3. 实现 Callable 接口 + FutureTask（有返回值、可抛异常）
 */
public class MyThread {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("主线程开始执行，线程名：" + Thread.currentThread().getName());

        // ========== 方式1：继承 Thread 类 ==========
        Thread1 thread1 = new Thread1();
        thread1.setName("继承Thread方式");
        thread1.start(); // 启动线程（必须调用 start()，不是 run()！）

        // ========== 方式2：实现 Runnable 接口 ==========
        Thread2 runnable = new Thread2();
        Thread thread2 = new Thread(runnable, "实现Runnable方式");
        thread2.start();
        // 使用labmda表达式简化 Runnable 方式
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println(Thread.currentThread().getName() + " 正在执行：" + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // ========== 方式3：实现 Callable 接口 + FutureTask（有返回值） ==========
        Callable<String> callable = new Thread3();
        FutureTask<String> futureTask = new FutureTask<>(callable);
        Thread thread3 = new Thread(futureTask, "实现Callable方式");
        thread3.start();
        // 使用lambda表达式简化 Callable 方式
        new Thread(new FutureTask<String>(() -> {for (int i = 0; i < 3; i++) {
            System.out.println(Thread.currentThread().getName() + " 正在执行：" + i);
            Thread.sleep(500);
        }
            return "线程执行成功！lambda方式返回";
        })).start();

        // 获取 Callable 线程的返回结果
        String result = futureTask.get();
        System.out.println("Callable 线程返回结果：" + result);

        System.out.println("主线程执行完毕");
    }
}

// =============================================
// 方式1：继承 Thread 类，重写 run() 方法
// =============================================
class Thread1 extends Thread {
    @Override
    public void run() {
        // 线程要执行的任务写在这里
        for (int i = 0; i < 3; i++) {
            System.out.println(getName() + " 正在执行：" + i);
            try {
                Thread.sleep(500); // 模拟任务耗时
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// =============================================
// 方式2：实现 Runnable 接口，重写 run() 方法
// =============================================
class Thread2 implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            System.out.println(Thread.currentThread().getName() + " 正在执行：" + i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// =============================================
// 方式3：实现 Callable 接口，重写 call() 方法
// 特点：有返回值、可以抛出异常
// =============================================
class Thread3 implements Callable<String> {
    @Override
    public String call() throws Exception {
        for (int i = 0; i < 3; i++) {
            System.out.println(Thread.currentThread().getName() + " 正在执行：" + i);
            Thread.sleep(500);
        }
        return "线程执行成功！"; // 返回结果
    }
}