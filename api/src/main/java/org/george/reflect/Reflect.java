package org.george.reflect;

import lombok.Data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflect {

    @Data
    static class User {
        private Long id;
        private String username;

        public void sayHello() {
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // 方式1：类名.class
        Class<User> clazz1 = User.class;

        // 方式2：对象.getClass()
        User user = new User();
        Class<?> clazz2 = user.getClass();

        // 方式3：全限定类名字符串
        Class<?> clazz3 = Class.forName("org.george.reflect.Reflect$User");


        // 获取无参构造
        Constructor<?> constructor = clazz1.getDeclaredConstructor();
        // 暴力访问（私有也能创建）
//        constructor.setAccessible(true);
        // 实例化
        User userByConstructor = (User) constructor.newInstance();
        System.out.println("userByConstructor = " + userByConstructor);
    }

    /**
     * 反射给对象字段设值
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Class<?> clazz = obj.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        // 突破私有访问权限
        field.setAccessible(true);
        field.set(obj, value);
    }

    /**
     * 反射获取对象字段值
     */
    public static Object getFieldValue(Object obj, String fieldName) throws Exception {
        Class<?> clazz = obj.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    /**
     * 反射调用对象方法
     */
    public static Object invokeMethod(Object obj, String methodName,
                                      Class<?>[] paramTypes, Object... args) throws Exception {
        Class<?> clazz = obj.getClass();
        Method method = clazz.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(obj, args);
    }

    public static void copyProperties(Object source, Object target) throws Exception {
        Class<?> sourceClazz = source.getClass();
        Class<?> targetClazz = target.getClass();

        // 获取所有字段
        Field[] sourceFields = sourceClazz.getDeclaredFields();
        for (Field sourceField : sourceFields) {
            String fieldName = sourceField.getName();
            try {
                // 目标类同名字段
                Field targetField = targetClazz.getDeclaredField(fieldName);
                sourceField.setAccessible(true);
                targetField.setAccessible(true);

                // 取值、赋值
                Object value = sourceField.get(source);
                targetField.set(target, value);
            } catch (NoSuchFieldException e) {
                // 目标没有该字段，直接跳过
                continue;
            }
        }
    }

}
