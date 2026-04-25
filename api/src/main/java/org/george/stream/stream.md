# Java Stream 流完全教程：从入门到精通
我会用**最通俗、最全面、最实战**的方式，带你彻底掌握 Java Stream 流！

Stream 是 Java 8 最核心的特性，专门用来**简化集合（List/Set/Map）的操作**（过滤、排序、统计、转换、去重…），告别繁琐的 for 循环，写出优雅高效的代码。

---

## 一、先搞懂：Stream 是什么？
一句话：**Stream 是集合的“流水线操作工具”**，把集合数据放到流水线上，一步步处理，最后输出结果。

### 三大特点
1. **不存储数据**：只是处理数据，不改变原集合
2. **惰性执行**：只有调用最终操作时才开始执行
3. **一次性使用**：流只能用一次，用完即销毁

### 标准使用流程（固定套路）
```java
集合.stream()        // 1. 获取流
    .中间操作()       // 2. 过滤、映射、排序...（可以多个）
    .终端操作();      // 3. 收集结果、统计、遍历...（必须有）
```

---

## 二、必备基础：创建 Stream 的 4 种方式
```java
import java.util.*;
import java.util.stream.*;

// 1. 从 List/Set 创建（最常用）
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream1 = list.stream();

// 2. 从数组创建
String[] arr = {"a", "b", "c"};
Stream<String> stream2 = Arrays.stream(arr);

// 3. 直接创建
Stream<String> stream3 = Stream.of("a", "b", "c");

// 4. 无限流（了解）
Stream<Integer> stream4 = Stream.iterate(0, n -> n + 1).limit(10);
```

---

## 三、【核心】Stream 所有常用方法分类详解
我把 Stream 方法分成 **2 大类**，你一看就懂：

### 第一类：中间操作（返回新流，可以链式调用）
### 1. filter(条件) —— **过滤**
保留满足条件的元素
```java
list.stream()
    .filter(s -> s.length() > 2)  // 保留长度>2的字符串
    .forEach(System.out::println);
```

### 2. map(转换函数) —— **类型转换/提取字段**
最常用！把一个对象转成另一个对象/提取属性
```java
// 例子：把字符串转成大写
list.stream().map(String::toUpperCase);

// 例子：从对象中提取字段（真实开发用爆）
List<User> userList = ...;
List<String> names = userList.stream()
                             .map(User::getName)  // 提取姓名
                             .collect(Collectors.toList());
```

### 3. flatMap —— **扁平化流**
把嵌套集合拆开（如 `List<List<Integer>>` → `List<Integer>`）
```java
List<List<Integer>> lists = Arrays.asList(Arrays.asList(1,2), Arrays.asList(3,4));
List<Integer> flat = lists.stream()
                          .flatMap(List::stream)
                          .collect(Collectors.toList());
// 结果：[1,2,3,4]
```

### 4. distinct() —— **去重**
```java
list.stream().distinct();
```

### 5. sorted() / sorted(Comparator) —— **排序**
```java
// 自然排序
list.stream().sorted();

// 自定义排序（年龄升序）
userList.stream().sorted(Comparator.comparingInt(User::getAge));
```

### 6. limit(n) —— 取前 n 个
### 7. skip(n) —— 跳过前 n 个
```java
list.stream().skip(2).limit(3);  // 跳过2个，取3个
```

### 8. peek() —— 调试用（不改变流，打印中间值）
```java
list.stream()
    .peek(s -> System.out.println("处理：" + s))
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

---

### 第二类：终端操作（执行并返回结果，流关闭）
### 1. forEach() —— **遍历**
```java
list.stream().forEach(System.out::println);
```

### 2. collect() —— **收集结果（最重要！）**
把流转成 List/Set/Map/字符串
```java
// 转 List
List<String> result = list.stream().collect(Collectors.toList());

// 转 Set
Set<String> set = list.stream().collect(Collectors.toSet());

// 转 Map（key:id，value:user）
Map<Integer, User> userMap = userList.stream()
    .collect(Collectors.toMap(User::getId, u -> u));

// 拼接字符串
String str = list.stream().collect(Collectors.joining(","));
```

### 3. count() —— 统计数量
```java
long count = list.stream().filter(s -> s.contains("a")).count();
```

### 4. 匹配判断（返回 boolean）
- `anyMatch()`：任意一个满足 → true
- `allMatch()`：全部满足 → true
- `noneMatch()`：全都不满足 → true
```java
boolean has = list.stream().anyMatch(s -> s.equals("abc"));
```

### 5. 获取元素
- `findFirst()`：获取第一个
- `findAny()`：获取任意一个（并行流常用）
```java
Optional<String> first = list.stream().findFirst();
```

### 6. 归约统计（数值计算）
`max/min/averagingInt/summingInt`
```java
// 求最大年龄
Optional<User> maxUser = userList.stream()
    .max(Comparator.comparingInt(User::getAge));

// 求和
int sum = userList.stream().mapToInt(User::getAge).sum();

// 平均值
double avg = userList.stream().mapToInt(User::getAge).average().orElse(0);
```

### 7. groupingBy —— **分组（超级常用）**
按字段分组，返回 Map
```java
// 按年龄分组
Map<Integer, List<User>> groupByAge = userList.stream()
    .collect(Collectors.groupingBy(User::getAge));
```

### 8. partitioningBy —— 分区（满足条件/不满足条件）
```java
Map<Boolean, List<User>> part = userList.stream()
    .collect(Collectors.partitioningBy(u -> u.getAge() > 18));
```

---

## 四、实战综合案例（必看！）
### 需求：
有一个用户列表，完成以下操作：
1. 过滤出 **成年（age≥18）** 的用户
2. 按 **年龄升序** 排序
3. 提取他们的 **姓名**
4. 转成 List 集合

```java
List<User> userList = Arrays.asList(
    new User(1, "张三", 17),
    new User(2, "李四", 22),
    new User(3, "王五", 19),
    new User(4, "赵六", 25)
);

// Stream 一行搞定
List<String> adultNames = userList.stream()
    .filter(u -> u.getAge() >= 18)    // 过滤成年
    .sorted(Comparator.comparingInt(User::getAge))  // 排序
    .map(User::getName)               // 提取姓名
    .collect(Collectors.toList());    // 收集结果
```

**结果**：`[王五, 李四, 赵六]`

---

## 五、Stream 必背技巧（开发高频）
1. **空指针安全**
```java
Optional.ofNullable(list)
        .orElseGet(Collections::emptyList)
        .stream()
        .filter(...);
```

2. **快速获取第一个元素**
```java
String first = list.stream()
                   .findFirst()
                   .orElse(null);
```

3. **数值流优化（int/double）**
```java
int totalAge = userList.stream()
                       .mapToInt(User::getAge)
                       .sum();
```

4. **Map 转 Stream**
```java
map.entrySet().stream()
              .filter(...)
              .forEach(...);
```

---

## 六、Stream 完整思维导图（帮你记忆）
```
Stream
├── 中间操作（流水线）
│   ├── filter 过滤
│   ├── map    转换/提取
│   ├── flatMap 扁平化
│   ├── distinct 去重
│   ├── sorted 排序
│   ├── limit  取前n个
│   ├── skip   跳过n个
│   └── peek   调试打印
└── 终端操作（执行结果）
    ├── forEach 遍历
    ├── collect 收集(List/Set/Map)
    ├── count   计数
    ├── anyMatch/allMatch/noneMatch 匹配
    ├── findFirst/findAny 获取元素
    ├── max/min/averaging/sum 统计
    ├── groupingBy 分组
    └── partitioningBy 分区
```

---

## 总结
你只要记住：
1. **Stream = 集合流水线**
2. **中间操作：处理数据**（filter/map/sorted）
3. **终端操作：输出结果**（collect/forEach/count）
4. **开发 90% 场景：filter + map + collect + groupingBy**

我可以**再给你出 10 道实战练习题**，让你彻底练会 Stream，再也不用写 for 循环！需要吗？