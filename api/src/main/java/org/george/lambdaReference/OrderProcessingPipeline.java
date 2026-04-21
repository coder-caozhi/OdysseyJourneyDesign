package org.george.lambdaReference;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 通用数据处理管道
 * 体现了 Lambda 四种写法 + Stream + Optional + 函数式接口
 * 
 * @param <T> 输入数据类型
 * @param <R> 输出数据类型
 */
public class OrderProcessingPipeline<T, R> {

    /**
     * 核心处理方法
     * 
     * @param sourceSupplier    (1. Supplier) 数据生产者：提供原始数据流
     * @param filterPredicate   (2. Predicate) 数据过滤器：定义筛选规则
     * @param mapperFunction    (3. Function) 数据转换器：定义转换逻辑
     * @param resultConsumer    (4. Consumer) 数据消费者：处理最终结果
     */
    public void process(Supplier<List<T>> sourceSupplier,
                        Predicate<T> filterPredicate,
                        Function<T, R> mapperFunction,
                        Consumer<List<R>> resultConsumer) {

        // 1. 获取数据 (使用 Supplier)
        List<T> rawData = sourceSupplier.get();

        // 2. 使用 Stream 流进行处理
        List<R> processedData = rawData.stream()
                // 使用 Optional 处理可能的空值（模拟防御性编程）
                .map(item -> Optional.ofNullable(item)
                        .filter(filterPredicate) // 应用 Predicate
                        .orElse(null))
                .filter(item -> item != null) // 过滤掉被 Optional 剔除的空值
                .map(mapperFunction)           // 应用 Function
                .collect(Collectors.toList());

        // 3. 消费结果 (使用 Consumer)
        resultConsumer.accept(processedData);
    }

    // --- 辅助类：模拟业务实体 ---
    static class Order {
        private String id;
        private double amount;
        private boolean paid;

        public Order(String id, double amount, boolean paid) {
            this.id = id;
            this.amount = amount;
            this.paid = paid;
        }
        public String getId() { return id; }
        public double getAmount() { return amount; }
        public boolean isPaid() { return paid; }
    }

    static class OrderDTO {
        private String orderId;
        private String status;

        public OrderDTO(String orderId, String status) {
            this.orderId = orderId;
            this.status = status;
        }

        @Override
        public String toString() {
            return "OrderDTO{id='" + orderId + "', status='" + status + "'}";
        }
    }
}