package org.george.lambdaReference;

import java.util.Arrays;
import java.util.List;

public class BusinessApp {
    public static void main(String[] args) {
        // 准备测试数据
        List<OrderProcessingPipeline.Order> orders = Arrays.asList(
            new OrderProcessingPipeline.Order("001", 100.0, true),
            new OrderProcessingPipeline.Order("002", 50.0, false),
            new OrderProcessingPipeline.Order("003", 200.0, true)
        );

        OrderProcessingPipeline<OrderProcessingPipeline.Order, OrderProcessingPipeline.OrderDTO> pipeline = 
            new OrderProcessingPipeline<>();

        // --- 开始组装管道 ---
        pipeline.process(
            
            // 1. Supplier 的写法 (提供数据源)
            // 写法 A：标准 Lambda
            // () -> orders
            // 写法 B：方法引用 (更简洁)
            () -> orders,

            // 2. Predicate 的写法 (过滤：只处理已支付的订单)
            // 写法 A：标准 Lambda
            // (order) -> order.isPaid()
            // 写法 B：方法引用
            OrderProcessingPipeline.Order::isPaid,

            // 3. Function 的写法 (转换：Order -> OrderDTO)
            // 写法 A：完整 Lambda (带大括号和 return)
            /*
            (order) -> {
                String status = order.getAmount() > 150 ? "VIP" : "Normal";
                return new OrderProcessingPipeline.OrderDTO(order.getId(), status);
            }
            */
            // 写法 B：表达式 Lambda (省略大括号和 return)
            (order) -> new OrderProcessingPipeline.OrderDTO(order.getId(), order.getAmount() > 150 ? "VIP" : "Normal"),
            // 写法 C：带类型声明的 Lambda (显式声明类型，虽然通常可以推断)
            // (OrderProcessingPipeline.Order order) -> new OrderProcessingPipeline.OrderDTO(order.getId(), "Normal"),

            // 4. Consumer 的写法 (消费：打印结果)
            // 写法 A：标准 Lambda
            // (list) -> list.forEach(System.out::println)
            // 写法 B：方法引用
            System.out::println
        );
    }
}