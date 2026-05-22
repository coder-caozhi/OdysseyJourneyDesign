package org.george.aop.controller;


import org.george.aop.annototation.NoRepeatLock;
import org.springframework.stereotype.Component;

@Component
public class MethodInterceptorController {

    @NoRepeatLock
    public void biz() {
        System.out.println("业务逻辑");
    }
}
