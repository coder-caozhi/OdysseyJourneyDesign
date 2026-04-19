package org.george.enumAndConstant;

/**
 * 用接口定义常量
 */
public interface ConstantConfig {
    // 接口中变量默认就是 public static final
    String PROJECT_NAME = "User Management System";
    int MAX_USER_COUNT = 1000;
    boolean ENABLE_CACHE = true;
    String DEFAULT_CHARSET = "UTF-8";

}