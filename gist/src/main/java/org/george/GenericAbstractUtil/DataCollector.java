package org.george.GenericAbstractUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据收集器工具类 - 展示 Consumer Super
 * 
 * 设计意图：
 * 我们希望把源数据写入目标集合。
 * 为了最大化灵活性，目标集合应该允许是 T 的任何父类。
 */
public class DataCollector {

    /**
     * 将源集合的所有元素添加到目标集合中
     * 
     * @param source 源数据。我们只从中读取，所以用 extends。
     * @param target 目标容器。我们要向其中写入 T，所以用 super。
     *               这意味着 target 可以是 List<T>, List<Object>, List<Serializable> 等。
     * @param <T>    数据类型
     */
    public static <T> void copyAll(List<? extends T> source, List<? super T> target) {
        for (T item : source) {
            // 编译器允许这样做，因为 target 声明为 ? super T
            // 这意味着 target 肯定能容纳 T 类型的对象
            target.add(item);
        }
    }

    // --- 测试代码 ---
    public static void main(String[] args) {
        List<Integer> numbers = List.of(1, 2, 3);
        
        // 目标集合定义为 Object 列表
        // 如果 target 定义为 List<Integer>，那就太死板了
        // 使用 ? super Integer，我们可以把 Integer 塞进 List<Number> 或 List<Object>
        List<Object> objectList = new ArrayList<>();
        
        // 调用成功：Integer 是 Object 的子类，List<Object> 匹配 ? super Integer
        copyAll(numbers, objectList);
        
        System.out.println("收集结果: " + objectList); // [1, 2, 3]
    }
}