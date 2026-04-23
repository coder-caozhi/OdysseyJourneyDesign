package org.george.debug;

public class DebugPractice {
    public static void main(String[] args) {
        // 1. 基础变量赋值
        int a = 10;
        int b = 20;
        String message = "开始调试练习";

        // 2. 调用方法
        int sum = add(a, b);
        System.out.println("a + b = " + sum);

        // 3. 循环练习
        int total = 0;
        for (int i = 1; i <= 5; i++) {
            total += i;
            System.out.println("当前循环 i = " + i + ", 累计 total = " + total);
        }

        // 4. 条件判断练习
        checkNumber(sum);

        // 5. 结束
        System.out.println(message + "，调试完成！");
    }

    /**
     * 加法方法（用于练习方法跳转调试）
     */
    public static int add(int num1, int num2) {
        int result = num1 + num2;
        return result;
    }

    /**
     * 数字判断方法（用于练习条件分支调试）
     */
    public static void checkNumber(int num) {
        if (num > 50) {
            System.out.println(num + " 大于 50");
        } else if (num > 30) {
            System.out.println(num + " 大于 30，小于等于 50");
        } else {
            System.out.println(num + " 小于等于 30");
        }
    }
}