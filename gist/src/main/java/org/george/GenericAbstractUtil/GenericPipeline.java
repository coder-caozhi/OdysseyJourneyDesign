package org.george.GenericAbstractUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GenericPipeline {

    /**
     * 通用批处理管道
     * 
     * 1. source: ? extends T (Producer) -> 我们从中读取 T
     * 2. target: ? super R (Consumer)  -> 我们向其中写入 R
     * 3. logic: Function<T, R>         -> 转换逻辑
     */
    public static <T, R> void processBatch(
            List<? extends T> source,
            List<? super R> target,
            Function<T, R> logic) {

        for (T item : source) {
            R result = logic.apply(item);
            target.add(result); // 安全写入
        }
    }

    public static void main(String[] args) {
        // 源数据：Integer
        List<Integer> rawData = List.of(10, 20, 30);
        
        // 目标容器：String (非常宽泛的类型)
        List<String> results = new ArrayList<>();
        
        // 逻辑：数字转字符串
        processBatch(rawData, results, num -> "数值: " + num);
        
        System.out.println(results); // [数值: 10, 数值: 20, 数值: 30]
    }
}