package org.george.GenericAbstractUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 数据转换工具类 - 展示 Producer Extends
 * 
 * 设计意图：
 * 我们希望这个方法足够通用，能够接受任何 T 的子类集合，
 * 并将它们转换成 R 类型的集合。
 */
public class CollectionMapper {

    /**
     * 通用映射方法
     * 
     * @param sourceList 源列表。使用 ? extends T，意味着我们可以传入 List<T> 或 List<任何T的子类>。
     *                   因为我们要从中“生产”数据，所以它是 Producer。
     * @param converter  转换逻辑（Lambda 表达式）
     * @param <T>        源数据的基类类型
     * @param <R>        目标数据的类型
     * @return 转换后的新列表
     */
    public static <T, R> List<R> map(List<? extends T> sourceList, Function<T, R> converter) {
        List<R> resultList = new ArrayList<>();
        
        // 这里的 item 被推断为 T 类型
        // 因为 sourceList 是 ? extends T，编译器保证取出来的元素至少是 T，
        // 所以可以安全地传给 converter
        for (T item : sourceList) {
            resultList.add(converter.apply(item));
        }
        
        return resultList;
    }

    // --- 测试代码 ---
    public static void main(String[] args) {
        // 模拟层级关系：User <- Employee <- Manager
        List<Employee> employees = List.of(new Employee("Alice"), new Employee("Bob"));
        List<Manager> managers = List.of(new Manager("Charlie"));

        // 场景 1：把 Employee 转成 String (名字)
        // List<Employee> 匹配 ? extends Employee
        List<String> names = map(employees, Employee::getName);
        System.out.println("员工名字: " + names); 

        // 场景 2：把 Manager 转成 String
        // List<Manager> 也能匹配 ? extends Employee (因为 Manager 是 Employee 的子类)
        // 这就是 PECS 的强大之处：多态性
        List<String> managerNames = map(managers, Employee::getName);
        System.out.println("经理名字: " + managerNames);
    }

    // 辅助类
    static class User { String name; User(String n) { name = n; } String getName() { return name; } }
    static class Employee extends User { Employee(String n) { super(n); } }
    static class Manager extends Employee { Manager(String n) { super(n); } }
}