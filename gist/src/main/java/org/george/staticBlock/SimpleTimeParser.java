package org.george.staticBlock;

import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * 简化版时间解析工具类 - 展示 static 代码块的作用
 */
public class SimpleTimeParser {

    /**
     * 通过 static 代码块初始化的解析器常量
     */
    private static final DateTimeFormatter TIME_FORMATTER;

    // 静态代码块：在类加载时执行一次，用于初始化复杂的对象
    static {
        System.out.println("执行 static 代码块，进行一次性的初始化...");
        
        // 第一步：创建基本格式
        DateTimeFormatter baseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        // 第二步：设置时区（模拟需要多步操作的场景）
        // 注意：在实际应用中，时区设置可能依赖于配置文件或系统属性
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        
        // 第三步：将格式器与时区结合（注意：Java 8+ DateTimeFormatter 需要 .withZone()）
        TIME_FORMATTER = baseFormatter.withZone(tz.toZoneId());
        
        System.out.println("static 代码块执行完毕，解析器初始化完成。");
    }

    /**
     * 解析时间字符串
     */
    public static java.time.LocalDateTime parse(String timeStr) {
        // 直接使用在 static 块中初始化好的解析器
        return java.time.LocalDateTime.parse(timeStr, TIME_FORMATTER);
    }
}

// --- 使用示例 ---
class StaticBlockDemo {
    public static void main(String[] args) {
        System.out.println("开始调用解析方法...");
        
        // 第一次调用，触发类加载，static 代码块会执行
        String timeStr = "2026-04-19 12:00:00";
        // 1.只能用于局部变量: var 只能在方法内部、构造函数内部或代码块内部声明局部变量时使用。它不能用于类成员变量（field）、方法参数或返回类型。
        // 2.必须立即初始化: 使用 var 声明变量时，必须在声明的同时进行初始化。编译器正是通过初始化表达式的类型来推断出变量的实际类型。
        // 3.编译时确定类型: var 不是动态类型，也不是 Object 类型。变量的类型在编译时就已经确定，并且在整个作用域内保持不变。
        // 4.增强可读性: 特别是在声明泛型集合时，var 可以显著减少代码冗余。
        var result1 = SimpleTimeParser.parse(timeStr);
        System.out.println("解析结果 1: " + result1);
        
        System.out.println("\n再次调用解析方法...");
        // 第二次调用，static 代码块不会再次执行，直接使用已初始化的对象
        var result2 = SimpleTimeParser.parse("2026-04-19 15:30:00");
        System.out.println("解析结果 2: " + result2);
    }
}