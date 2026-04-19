package org.george.enumAndConstant;

public enum HttpStatus {
    // 定义常量时传入参数，调用构造器
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Server Error"); // 注意这里的分号不能省略，因为这些变量已经定义了属性

    // 私有属性
    private final int code;
    private final String message;

    // 构造器必须是 private 或默认权限 设计思想：枚举的本质是一组固定的常量集合，确保实例的唯一性
    //如果允许 public 构造器，意味着外部代码可以通过 new 关键字随意创建枚举的新实例（例如 new Day()）。这将导致枚举常量的数量不再固定，破坏了枚举“有限集合”的语义。
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

    // 也可以设置通用方法 通常是通过一个属性得到另外一个属性
    //TODO:结合枚举类型自带方法，通过Map方式获取最佳时间复杂度
    public void printInfo() {
        System.out.println(code + message);
    }

    // 编译器会为每个枚举类型自动添加一些有用的方法。
    // 1. 根据常量值获取实例
    public HttpStatus getInstanceByString(String name) {
        return HttpStatus.valueOf("name");
    }
    // 2. 放回包含所有枚举常量的数组
    public HttpStatus[] getAllConstants() {
        return HttpStatus.values();
    }
    // 3. 返回枚举常量的名称
    public String getName(HttpStatus httpStatus) {
        return httpStatus.name();
    }
    // 4. 返回枚举常量声明顺序位置（索引）
    public int getIndex(HttpStatus httpStatus) {
        return httpStatus.ordinal();
    }
}