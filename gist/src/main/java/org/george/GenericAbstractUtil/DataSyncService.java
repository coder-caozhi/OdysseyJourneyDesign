package org.george.GenericAbstractUtil;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 通用数据同步服务
 * 
 * 设计思想：
 * 我们不关心具体的业务实体是什么（User, Order, Product），
 * 我们只关心数据的流动：从 Source 流向 Target。
 * 
 * @param <T> 数据实体的基准类型（例如：BaseEntity）
 */
public class DataSyncService<T> {

    /**
     * 核心同步方法
     * 
     * @param source 数据源（Producer）。
     *               使用 ? extends T，意味着我们可以接受任何 T 的子类集合。
     *               例如：T 是 User，这里可以是 List<VipUser>。
     * @param target 数据目标（Consumer）。
     *               使用 ? super T，意味着我们可以接受任何 T 的父类集合。
     *               例如：T 是 User，这里可以是 List<Object> 或 List<Serializable>。
     * @param converter 数据转换器（可选，用于清洗数据）
     */
    public void sync(List<? extends T> source, List<? super T> target, Function<T, T> converter) {
        
        // 1. 从 Producer (source) 读取数据
        // 安全：因为 source 是 ? extends T，取出来的元素一定是 T（或子类），可以安全传入 converter
        for (T item : source) {
            T processedItem = converter.apply(item);
            
            // 2. 向 Consumer (target) 写入数据
            // 安全：因为 target 是 ? super T，它一定能容纳 T 类型的对象
            target.add(processedItem);
        }
    }
}