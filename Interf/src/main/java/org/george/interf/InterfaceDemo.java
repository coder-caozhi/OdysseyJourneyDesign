package org.george.interf;

// 1. 定义一个包含多种方法的接口
interface PaymentService {
    // 【抽象方法】：没有方法体，强制所有实现类必须自己实现
    void pay(double amount);

    // 【默认方法 (default)】：带有具体实现，实现类可以直接继承使用，也可以按需重写
    default void refund(double amount) {
        System.out.println("【默认退款逻辑】：正在原路退回 " + amount + " 元...");
    }

    // 【静态方法 (static)】：属于接口本身，只能通过接口名直接调用，不能被继承或重写
    static void printWelcome() {
        System.out.println("【静态方法】：欢迎使用统一支付平台！");
    }
}

// 2. 实现类 A：只实现抽象方法，直接“躺平”继承默认方法
class AliPay implements PaymentService {
    @Override
    public void pay(double amount) {
        System.out.println("【支付宝】：使用支付宝成功支付 " + amount + " 元。");
    }
}

// 3. 实现类 B：实现抽象方法，并且觉得默认退款逻辑不适合自己，选择重写 (Override) 默认方法
class WechatPay implements PaymentService {
    @Override
    public void pay(double amount) {
        System.out.println("【微信支付】：使用微信支付成功支付 " + amount + " 元。");
    }

    // 重写 default 方法（注意：重写时不需要再写 default 关键字）
    @Override
    public void refund(double amount) {
        System.out.println("【微信支付】：微信退款需人工审核，正在提交 " + amount + " 元的退款申请...");
    }
}

public class InterfaceDemo {
    public static void main(String[] args) {
        // 调用接口的静态方法（必须用 接口名.方法名 调用）
        PaymentService.printWelcome();
        System.out.println("-------------------------");

        // 测试支付宝（继承默认退款方法）
        PaymentService aliPay = new AliPay();
        aliPay.pay(100.0);
        aliPay.refund(50.0); // 调用的是接口里自带的默认逻辑
        System.out.println("-------------------------");

        // 测试微信支付（重写了默认退款方法）
        PaymentService wechatPay = new WechatPay();
        wechatPay.pay(200.0);
        wechatPay.refund(100.0); // 调用的是微信自己重写的退款逻辑
    }
}