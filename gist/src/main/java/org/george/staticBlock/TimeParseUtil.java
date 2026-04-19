package org.george.staticBlock;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间解析工具类 - 展示通过静态初始化提升性能
 */
public class TimeParseUtil {

    /**
     * 在类加载时初始化时间解析器，避免每次调用方法时重复创建。
     * DateTimeFormatter 是线程安全的，可以安全地共享。
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 解析时间字符串为 LocalDateTime 对象。
     * 因为 FORMATTER 是静态的，只创建一次，所以此方法性能很高。
     *
     * @param timeStr 符合 "yyyy-MM-dd HH:mm:ss" 格式的字符串
     * @return 对应的 LocalDateTime 对象
     */
    public static LocalDateTime parseTime(String timeStr) {
        // 直接复用已创建的 FORMATTER，无需每次都 new 一个新的
        return LocalDateTime.parse(timeStr, FORMATTER);
    }

    // --- 性能对比：低效写法（仅作演示） ---
    // public static LocalDateTime parseTimeInefficiently(String timeStr) {
    //     // 每次调用都创建一个新的 DateTimeFormatter 实例，非常消耗资源
    //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //     return LocalDateTime.parse(timeStr, formatter);
    // }
}

// 使用示例
class Example {
    public static void main(String[] args) {
        String timeString = "2026-04-18 15:30:45";
        
        LocalDateTime parsedTime = TimeParseUtil.parseTime(timeString);
        System.out.println("解析结果: " + parsedTime); // 输出: 2026-04-18T15:30:45
    }
}