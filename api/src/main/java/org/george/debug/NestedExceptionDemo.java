package org.george.debug;

public class NestedExceptionDemo {
    public static void main(String[] args) {
        try {
            businessService();
        } catch (Exception e) {
            // 把外层异常对象 e 丢进调试窗口，你就能看到 cause 字段
            System.out.println("外层异常信息：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 业务层，会把底层异常包装起来
    private static void businessService() {
        try {
            daoLayer();
        } catch (ArithmeticException e) {
            // 关键：把原始异常 e 设为 cause，抛出新的业务异常
            throw new RuntimeException("订单结算失败", e);
        }
    }

    // 数据层，抛出原始异常
    private static void daoLayer() {
        int a = 100;
        int b = 0;
        // 这里抛出原始 ArithmeticException
        int result = a / b;
    }
    /**
     * Debug 运行后，你会在调试窗口看到这样的效果
     * 最外层的 Exception 是 RuntimeException，信息是 "订单结算失败"
     * 它的 cause 字段，指向的是底层的 ArithmeticException，信息是 "/ by zero"
     * 两个对象是完全不同的实例，@ 后面的内存地址也不一样
     * 这时候你就懂了：
     * 没有包装的原生异常：cause 指向自己
     * 被包装的异常：cause 指向真正的 “病根” 异常
     */
}