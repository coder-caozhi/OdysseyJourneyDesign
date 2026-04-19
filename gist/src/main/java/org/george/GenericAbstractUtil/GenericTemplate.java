package org.george.GenericAbstractUtil;

import java.util.ArrayList;
import java.util.List;

// 1. 泛型类
public class GenericTemplate<T> {
    
    private T data;

    public GenericTemplate(T data) {
        this.data = data;
    }

    // 2. 使用类的泛型 T
    public T getData() {
        return data;
    }

    // 3. 泛型方法（非静态）：可以使用类的 T
    public void printData() {
        System.out.println(data);
    }

    // 4. 泛型方法（独立泛型）：定义自己的 <E>
    public <E> void printGeneric(E e) {
        System.out.println(e);
    }

    // 5. 静态泛型方法：必须自己定义 <E>
    public static <E> void staticPrint(E e) {
        System.out.println("Static: " + e);
    }

    // 6. 使用 PECS 的方法
    // 从 source 读取 T (Producer -> extends)
    public void copyFrom(List<? extends T> source) {
        for (T item : source) {
            System.out.println(item);
        }
    }

    // 向 target 写入 T (Consumer -> super)
    public void copyTo(List<? super T> target) {
        target.add(data);
    }

    public static void main(String[] args) {
        // 测试
        GenericTemplate<String> template = new GenericTemplate<>("Hello");
        template.printData();
        template.printGeneric(123); // E 推断为 Integer
        GenericTemplate.staticPrint("World"); // 静态调用
    }
}