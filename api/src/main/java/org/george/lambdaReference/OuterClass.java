package org.george.lambdaReference;

public class OuterClass {
    private int outerField = 100;
    private static int staticOuterField = 200;

    // ==========================================
    // 1. 成员内部类 (Member Inner Class)
    // ==========================================
    // 特点：像成员变量一样，属于外部类的“实例”，可以访问外部类的所有成员（包括私有）
    class MemberInner {
        public void show() {
            System.out.println("成员内部类: " + outerField); // 直接访问私有实例变量
        }
    }

    // ==========================================
    // 2. 静态内部类 (Static Inner Class)
    // ==========================================
    // 特点：属于外部类的“类”级别，只能访问外部类的静态成员
    static class StaticInner {
        public void show() {
            // System.out.println(outerField); // 报错！不能访问实例变量
            System.out.println("静态内部类: " + staticOuterField); // 可以访问静态变量
        }
    }

    // ==========================================
    // 3. 局部内部类 (Local Inner Class)
    // ==========================================
    // 特点：定义在方法里，作用域仅限于该方法
    public void methodWithLocalInner() {
        int localVar = 50; // JDK 1.8+ 要求 effectively final

        class LocalInner {
            public void show() {
                System.out.println("局部内部类: " + outerField); // 可以访问外部类成员
                System.out.println("局部内部类: " + localVar);   // 可以访问局部变量
            }
        }

        // 在方法内部使用
        new LocalInner().show();
    }

    // ==========================================
    // 4. 匿名内部类 (Anonymous Inner Class)
    // ==========================================
    // 特点：没有名字，通常用于实现接口或继承抽象类，是一次性使用的类
    public void methodWithAnonymousInner() {
        // 假设有一个接口
        // interface Flyable { void fly(); }

        Flyable f = new Flyable() {
            @Override
            public void fly() {
                System.out.println("匿名内部类: 实现了接口方法");
            }
        };
        f.fly();
    }

    // 测试主方法
    public static void main(String[] args) {
        OuterClass outer = new OuterClass();

        // 1. 成员内部类的创建方式：先有外部类对象，再 .new
        MemberInner m = outer.new MemberInner();
        m.show();

        // 2. 静态内部类的创建方式：直接 new，不需要外部类对象
        StaticInner s = new StaticInner();
        s.show();

        // 3. 局部内部类：只能在方法里调
        outer.methodWithLocalInner();

        // 4. 匿名内部类：只能在方法里调
        outer.methodWithAnonymousInner();
    }
}

// 辅助接口，用于演示匿名内部类
interface Flyable {
    void fly();
}


