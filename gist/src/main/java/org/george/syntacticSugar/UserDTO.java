package org.george.syntacticSugar;

import java.util.Objects;

//
// 1. 需要手动定义所有字段为 final
public final class UserDTO {
    private final Long id;
    private final String username;
    private final String email;

    // 2. 需要手动编写全参构造函数 final属性一定是要全参构造器创建
    public UserDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}