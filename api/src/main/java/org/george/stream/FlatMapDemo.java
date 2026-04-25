package org.george.stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlatMapDemo {
    public static void main(String[] args) {
        // 嵌套集合：班级 -> 学生
        List<List<String>> classStudentList = Arrays.asList(
                Arrays.asList("张三", "李四"),
                Arrays.asList("王五", "赵六"),
                Arrays.asList("钱七")
        );

        // flatMap：把每个子集合转成流，再合并成一个流
        List<String> allStudent = classStudentList.stream()
                // 把每一个 List<String> 转成 Stream<String>，然后合并拍平
                .flatMap(subList -> subList.stream())
                .collect(Collectors.toList());

        System.out.println(allStudent);
    }
}