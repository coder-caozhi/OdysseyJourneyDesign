package org.george.debug;

public class ExceptionDebugDemo {
    public static void main(String[] args) {
        // 1. 算术异常：除0
        try {
            devideZero();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 2. 数组下标越界异常
//        arrayIndexOut();

        // 3. 空指针异常
//        nullPointTest();
    }

    // 算术异常：ArithmeticException / by zero
    public static void devideZero() {
        System.out.println("start to caculate");
        int a = 10;
        int b = 0;
        // 这里会直接崩溃
       int res = a / b;
        System.out.println("结果：" + res);
    }

    // 数组越界：ArrayIndexOutOfBoundsException
    public static void arrayIndexOut() {
        int[] arr = {11, 22, 33};
        // 只有0、1、2下标，访问5必报错
        int num = arr[5];
        System.out.println("数组值：" + num);
    }

    // 空指针：NullPointerException
    public static void nullPointTest() {
        String str = null;
        // 空对象调用方法，直接空指针
        int len = str.length();
        System.out.println("字符串长度：" + len);
    }
}