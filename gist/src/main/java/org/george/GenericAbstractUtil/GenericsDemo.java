package org.george.GenericAbstractUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 泛型 Generics 完整演示 - 可直接运行
 */
public class GenericsDemo {
    
    // ==================== 1. 泛型类 ====================
    static class Box<T> {
        private T content;
        
        public void set(T content) {
            this.content = content;
        }
        
        public T get() {
            return content;
        }
        
        public String getType() {
            return content != null ? content.getClass().getSimpleName() : "null";
        }
    }
    
    // ==================== 2. 泛型方法 ====================
    public static <T> void printArray(T[] array) {
        System.out.print("数组内容: [");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
            if (i < array.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }
    
    // ==================== 3. 泛型接口 ====================
    interface Processor<T> {
        T process(T input);
    }
    
    // 实现泛型接口
    static class StringProcessor implements Processor<String> {
        @Override
        public String process(String input) {
            return input.toUpperCase();
        }
    }
    
    // ==================== 4. 泛型边界 ====================
    static class NumberBox<T extends Number> {  // T必须是Number或其子类
        private T value;
        
        public NumberBox(T value) {
            this.value = value;
        }
        
        public double getDoubleValue() {
            return value.doubleValue();
        }
    }
    
    // ==================== 5. 通配符演示 ====================
    public static void printList(List<?> list) {  // 任意类型
        System.out.println("列表大小: " + list.size());
        for (Object obj : list) {
            System.out.println("  - " + obj);
        }
    }
    
    // ==================== 主方法：运行所有演示 ====================
    public static void main(String[] args) {
        System.out.println("========== 泛型 Generics 演示 ==========\n");
        
        // 1. 泛型类演示
        System.out.println("【1. 泛型类 Box<T>】");
        Box<String> stringBox = new Box<>();
        stringBox.set("Hello Generics");
        System.out.println("  内容: " + stringBox.get());
        System.out.println("  类型: " + stringBox.getType());
        
        Box<Integer> intBox = new Box<>();
        intBox.set(2024);
        System.out.println("  内容: " + intBox.get());
        System.out.println("  类型: " + intBox.getType());
        System.out.println();
        
        // 2. 泛型方法演示
        System.out.println("【2. 泛型方法 printArray(T[])】");
        String[] strArray = {"Java", "Python", "Go"};
        Integer[] intArray = {1, 2, 3, 4, 5};
        printArray(strArray);
        printArray(intArray);
        System.out.println();
        
        // 3. 泛型接口演示
        System.out.println("【3. 泛型接口 Processor<T>】");
        StringProcessor processor = new StringProcessor();
        String result = processor.process("hello world");
        System.out.println("  处理结果: " + result);
        System.out.println();
        
        // 4. 泛型边界演示
        System.out.println("【4. 泛型边界 NumberBox<T extends Number>】");
        NumberBox<Integer> intNumBox = new NumberBox<>(100);
        NumberBox<Double> doubleNumBox = new NumberBox<>(3.14);
        System.out.println("  Integer转double: " + intNumBox.getDoubleValue());
        System.out.println("  Double值: " + doubleNumBox.getDoubleValue());
        // NumberBox<String> strNumBox = new NumberBox<>("abc"); // 编译错误！String不是Number子类
        System.out.println();
        
        // 5. 通配符演示
        System.out.println("【5. 通配符 List<?>】");
        List<String> strList = new ArrayList<>();
        strList.add("Apple");
        strList.add("Banana");
        List<Integer> intList = new ArrayList<>();
        intList.add(10);
        intList.add(20);
        
        System.out.println("  字符串列表:");
        printList(strList);
        System.out.println("  整数列表:");
        printList(intList);
        System.out.println();
        
        // 6. 对比：不用泛型 vs 用泛型
        System.out.println("【6. 类型安全对比】");
        List rawList = new ArrayList();  // 原始类型（不推荐）
        rawList.add("字符串");
        rawList.add(123);  // 不会报错，但危险！
        
        List<String> safeList = new ArrayList<>();  // 泛型类型
        safeList.add("只能放字符串");
        // safeList.add(456);  // 编译错误！类型安全
        
        System.out.println("  原始列表可混存任意类型（危险）");
        System.out.println("  泛型列表强制类型安全（推荐）");
        
        System.out.println("\n========== 演示结束 ==========");
    }
}