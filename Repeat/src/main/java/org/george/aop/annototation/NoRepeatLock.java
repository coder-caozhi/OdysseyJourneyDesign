package org.george.aop.annototation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface NoRepeatLock {
    /**
     * 默认1秒
     *
     * @return
     */
    int limitTime() default 1;

    /**
     * 1秒允许访问一次
     *
     * @return
     */
    int allowAccessTimes() default 1;
}