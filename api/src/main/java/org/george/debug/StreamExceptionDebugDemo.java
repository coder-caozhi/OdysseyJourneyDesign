package org.george.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stream 流【故意埋异常】调试专用
 * 超长链式调用，中间故意制造空指针、除0异常、数据null异常
 * 专门用来练习：Stream异常断点、看堆栈、定位lambda里的错误
 */
public class StreamExceptionDebugDemo {
    public static void main(String[] args) {
        List<Student> studentList = buildStudentData();
        // stream放在一行
        List<String> resultFlap = studentList.stream().filter(s -> s.getAge() > 15).map(s -> s.getSchool().getName()).map(name -> calcScore(name)).sorted().skip(1).limit(3).collect(Collectors.toList());

        // 超长Stream调用链：filter -> map -> map -> sorted -> skip -> limit -> collect
        // 里面故意埋了多处会报错的逻辑
        List<String> result = studentList.stream()
                // 1. 过滤
                .filter(s -> s.getAge() > 15)
                // 2. 第一次映射：故意取null对象属性
                .map(s -> s.getSchool().getName())
                // 3. 第二次映射：故意做除法除0
                .map(name -> calcScore(name))
                // 4. 排序
                .sorted()
                // 5. 跳过、限制
                .skip(1)
                .limit(3)
                // 终端收集
                .collect(Collectors.toList());

        System.out.println(resultFlap);
        System.out.println(result);
    }

    // 故意写一个会除0报错的方法
    private static String calcScore(String name) {
        int a = 100;
        int b = 0;
        // 算术异常：/ by zero
        int res = a / b;
        return name + "-" + res;
    }

    // 构造测试数据：故意放一个 school = null 的对象
    private static List<Student> buildStudentData() {
        List<Student> list = new ArrayList<>();
        list.add(new Student("小明", 18, new School("阳光中学")));
        // 关键：这个学生 school 为 null，后面调用 getName() 必空指针
        list.add(new Student("小红", 17, null));
        list.add(new Student("小刚", 20, new School("实验中学")));
        return list;
    }

    static class Student {
        private String name;
        private Integer age;
        private School school;

        public Student(String name, Integer age, School school) {
            this.name = name;
            this.age = age;
            this.school = school;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

        public School getSchool() {
            return school;
        }
    }

    static class School {
        private String name;

        public School(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}