package org.george.syntacticSugar;

/**
 * record语法糖
 * @param id
 * @param username
 * @param email
 */
// record 是为了“纯粹的数据”和“不可变性”而生的
// 不可变的数据载体 (透明数据)，创建不可变的数据容器场景使用
public record UserDTOSugar(Long id, String username, String email){};

/*
极致的简洁（减少样板代码）
编译器会自动根据括号内的参数列表生成 private final 的字段。
自动生成公开的访问器方法（Accessor Methods）。注意：方法名与字段名相同（例如 id() 而不是 getId()）。
自动实现 equals() 和 hashCode()，基于所有字段进行比较。
自动实现 toString()，格式清晰（如 UserDTO[id=1, username=alice, email=...]）。
默认不可变（Immutable）
record 的所有字段默认都是 private final 的。这意味着一旦对象被创建，其状态就不能被修改。这对于多线程安全和作为 Map 的 Key 非常重要。
它没有 Setter 方法。
语义明确
看到 class，我们通常认为它是一个包含行为的实体（有业务逻辑）。
看到 record，我们立刻知道它只是一个纯粹的数据容器，类似于数据库中的一行记录。
支持自定义逻辑（灵活性）
虽然它是自动生成的，但你仍然可以添加自定义逻辑，比如参数校验或自定义构造函数。
*/
