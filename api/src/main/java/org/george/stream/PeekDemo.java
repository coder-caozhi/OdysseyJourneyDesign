package org.george.stream;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

public class PeekDemo {

    @Data
    @AllArgsConstructor
    static class User {
        private String name;
        private Integer age;
    }

    public static void main(String[] args) {
        List<String> list = List.of("Java", "Stream", "Spring");

        List<String> res = list.stream()
                .peek(s -> System.out.println("原始元素：" + s)) // 偷看打印
                .filter(s -> s.length() > 4)
                .peek(s -> System.out.println("过滤后：" + s))  // 过滤后再偷看
                .collect(Collectors.toList());

        System.out.println("最终结果：" + res);

        List<User> userList = List.of(
                new User("张三", 18),
                new User("李四", 22)
        );

        List<User> newUserList = userList.stream()
                .peek(user -> {
                    // 直接修改对象属性
                    user.setAge(user.getAge() + 2);
                    user.setName(user.getName() + "_VIP");
                })
                .collect(Collectors.toList());

        System.out.println(newUserList);
    }
}