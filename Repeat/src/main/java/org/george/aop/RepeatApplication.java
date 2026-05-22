package org.george.aop;

import org.george.aop.controller.MethodInterceptorController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class RepeatApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(RepeatApplication.class, args);
        MethodInterceptorController controller = context.getBean(MethodInterceptorController.class);
        controller.biz();
    }
}
