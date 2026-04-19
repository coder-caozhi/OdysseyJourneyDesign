package org.george.GenericAbstractUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// --- 1. 定义业务实体层级 ---

// 基类
class BaseEntity {
    private Long id;
    public BaseEntity(Long id) { this.id = id; }
    public Long getId() { return id; }
}

// 具体业务类：用户
class User extends BaseEntity {
    private String name;
    public User(Long id, String name) {
        super(id);
        this.name = name;
    }
    @Override
    public String toString() {
        return "User{id=" + getId() + ", name='" + name + "'}";
    }
}

// 具体业务类：VIP用户（User的子类）
class VipUser extends User {
    private String vipLevel;
    public VipUser(Long id, String name, String vipLevel) {
        super(id, name);
        this.vipLevel = vipLevel;
    }
}

// --- 2. 模拟业务调用 ---

public class BusinessApp {
    public static void main(String[] args) {
        // 初始化同步服务，基准类型定为 User
        DataSyncService<User> syncService = new DataSyncService<>();

        // --- 场景 A：体现 Producer (? extends) ---
        // 我们的数据源是 VipUser（User 的子类）
        // 在实际业务中，这可能是从 VIP 接口拉取的数据
        List<VipUser> vipUsers = Arrays.asList(
            new VipUser(1L, "Alice", "Gold"),
            new VipUser(2L, "Bob", "Silver")
        );

        // 目标是普通的 User 列表
        List<User> userDatabase = new ArrayList<>();

        // 调用 sync
        // 为什么能传 List<VipUser>？
        // 因为 sync 方法参数是 List<? extends User>。
        // VipUser 是 User 的子类，所以 List<VipUser> 匹配 ? extends User。
        // 这就是 Producer 的灵活性：允许更具体的子类数据源。
        syncService.sync(vipUsers, userDatabase, user -> user);

        System.out.println("同步后的用户库: " + userDatabase);
        // 输出：[User{id=1, name='Alice'}, User{id=2, name='Bob'}]

        // --- 场景 B：体现 Consumer (? super) ---
        // 假设我们需要把 User 数据同步到一个通用的“对象池”或者“日志缓冲区”
        // 这个池子定义的是 List<Object> 或者 List<Serializable>
        List<Object> genericObjectPool = new ArrayList<>();

        // 再次调用 sync，这次源是普通的 User
        List<User> normalUsers = Arrays.asList(new User(3L, "Charlie"));
        
        // 为什么能传 List<Object>？
        // 因为 sync 方法参数是 List<? super User>。
        // Object 是 User 的父类，所以 List<Object> 匹配 ? super User。
        // 这就是 Consumer 的灵活性：允许更宽泛的父类容器。
        syncService.sync(normalUsers, genericObjectPool, user -> user);

        System.out.println("通用对象池大小: " + genericObjectPool.size());
        // 输出：1
    }
}