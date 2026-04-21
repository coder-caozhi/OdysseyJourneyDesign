package org.george.optional;

import java.util.List;
import java.util.Optional;

/**
 * optional解决if else冗余复杂的写法
 * 生产级 Optional 高频用法最佳实践
 * 包含：ofNullable、map、filter、orElseGet、ifPresent、orElseThrow
 */
public class OptionalBestPracticeDemo {

    // 模拟业务实体
    static class User {
        private Long id;
        private String username;
        private List<String> roles;

        // Getter
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public List<String> getRoles() { return roles; }
    }

    // ======================= 最常用、最核心的 5 种用法 =======================

    /**
     * 1. 安全获取值，为空返回默认值（最常用）
     */
    public String getUsernameSafe(User user) {
        return Optional.ofNullable(user)      // 允许 null
                .map(User::getUsername)       // 链式获取属性
                .orElseGet(() -> "匿名用户"); // 空则返回默认（延迟执行）
    }

    /**
     * 2. 非空才执行逻辑（ifPresent 最常用）
     */
    public void doIfUserNotNull(User user) {
        Optional.ofNullable(user)
                .ifPresent(u -> System.out.println("用户存在：" + u.getUsername()));
    }

    /**
     * 3. 空值抛出业务异常（企业接口必用）
     */
    public User checkUserNotNull(User user) {
        return Optional.ofNullable(user)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    /**
     * 4. 链式多层获取，防 NPE（经典！）
     */
    public String getFirstRole(User user) {
        return Optional.ofNullable(user)
                .map(User::getRoles)          // 第一层
                .filter(roles -> !roles.isEmpty()) // 过滤空集合
                .map(roles -> roles.get(0))   // 第二层
                .orElse("无角色");
    }

    /**
     * 5. 条件过滤（filter 经典用法）
     */
    public User getValidUser(User user) {
        return Optional.ofNullable(user)
                .filter(u -> u.getId() != null) // 只保留合法用户
                .orElse(null);
    }
}