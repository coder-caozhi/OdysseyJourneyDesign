




Java 枚举（enum）是一种特殊的类，用于定义一组固定的常量。它提供了比使用 public static final 常量更好的类型安全性和代码可读性。

📝 基本语法

最简单的枚举定义如下，使用 enum 关键字，后跟枚举名称和用大括号括起来的一组常量。

public enum Day {
MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

每个常量（如 MONDAY）都是该枚举类型的一个公共、静态且不可变的实例。
多个常量之间用逗号 , 分隔。
如果枚举只有常量，末尾的分号 ; 可以省略。

🛠️ 进阶用法

枚举可以像普通类一样，拥有属性、构造器和方法。

带属性和构造器的枚举

可以为每个枚举常量关联数据。

public enum HttpStatus {
// 定义常量时传入参数，调用构造器
OK(200, "OK"),
NOT_FOUND(404, "Not Found"),
INTERNAL_SERVER_ERROR(500, "Server Error"); // 注意这里的分号不能省略

    // 私有属性
    private final int code;
    private final String message;

    // 构造器必须是 private 或默认权限
    private HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // Getter 方法
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

使用方式：
HttpStatus status = HttpStatus.OK;
System.out.println(status.getCode()); // 输出: 200
System.out.println(status.getMessage()); // 输出: OK

实现接口的枚举

枚举可以 implements 一个或多个接口，从而为所有常量定义统一的行为。

// 定义一个接口
public interface Describable {
String getDescription();
}

// 枚举实现接口
public enum Season implements Describable {
SPRING {
@Override
public String getDescription() {
return "万物复苏";
}
},
SUMMER {
@Override
public String getDescription() {
return "烈日炎炎";
}
};

    // 也可以有通用方法
    public void printInfo() {
        System.out.println(this + ": " + getDescription());
    }
}

🔑 常用方法

编译器会为每个枚举类型自动添加一些有用的方法。
方法签名   作用   示例
values()   返回包含所有枚举常量的数组   Day[] days = Day.values();

valueOf(String name)   根据常量名（字符串）获取枚举实例   Day monday = Day.valueOf("MONDAY");

name()   返回枚举常量的名称   String name = Day.MONDAY.name();

ordinal()   返回枚举常量在声明中的索引（从0开始）   int index = Day.MONDAY.ordinal(); // 0

🎯 主要特点

类型安全：限制了变量的取值范围，只能是预定义的常量，避免了传入无效值。
隐式继承：所有枚举都隐式继承自 java.lang.Enum 类，因此不能再继承其他类。
构造器私有：枚举的构造器默认是私有的，无法在外部通过 new 关键字创建实例。
不可继承：枚举类是 final 的，不能被其他类继承。
完美配合 switch：枚举可以非常方便地与 switch 语句一起使用，使代码逻辑更清晰。