package org.george.debug;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 超长链式 Stream 流 Debug 专用练习
 * 适合练习 IDEA 调试 Stream 每一步中间结果
 */
public class StreamLongChainDebugDemo {

    public static void main(String[] args) {
        // 1. 构造模拟用户数据
        List<User> userList = buildUserData();

        // 2. 超长 Stream 调用链，专门用来调试
        // 链式：过滤 -> 映射 -> 去重 -> 排序 -> 跳过 -> 限制 -> 分组 -> 收集
        List<String> resultList = userList.stream()
                // 中间操作1：过滤 年龄大于18、性别为男
                .filter(user -> user.getAge() > 18 && "男".equals(user.getGender()))
                // 中间操作2：映射 只获取用户名+拼接年龄
                .map(user -> user.getName() + "-" + user.getAge())
                // 中间操作3：去重
                .distinct()
                // 中间操作4：按字符串自然排序
                .sorted()
                // 中间操作5：跳过前2个
                .skip(2)
                // 中间操作6：只取前5个
                .limit(5)
                // 终端操作：转为List
                .collect(Collectors.toList());


        System.out.println("最终收集结果：");
        resultList.forEach(System.out::println);


        // 再加一条更复杂的Stream：分组+统计，用来进阶调试
        Map<Integer, List<User>> groupMap = userList.stream()
                .filter(u -> u.getScore() >= 60)
                .sorted(Comparator.comparingInt(User::getScore).reversed())
                .collect(Collectors.groupingBy(User::getAge));

        System.out.println("\n分组结果：" + groupMap);
    }


    /**
     * 构造模拟数据
     */
    private static List<User> buildUserData() {
        List<User> list = new ArrayList<>();
        list.add(new User("张三", 20, "男", 85));
        list.add(new User("李四", 17, "女", 59));
        list.add(new User("王五", 25, "男", 92));
        list.add(new User("张三", 20, "男", 85));
        list.add(new User("赵六", 30, "男", 77));
        list.add(new User("小红", 22, "女", 88));
        list.add(new User("小刚", 28, "男", 66));
        list.add(new User("小明", 19, "男", 55));
        return list;
    }


    /**
     * 内部实体类
     */
    @Data
    @AllArgsConstructor
    static class User {
        private String name;
        private Integer age;
        private String gender;
        private Integer score;
    }
}