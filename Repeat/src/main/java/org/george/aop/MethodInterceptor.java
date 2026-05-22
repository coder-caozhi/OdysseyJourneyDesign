package org.george.aop;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.george.aop.annototation.NoRepeatLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Aspect
@Component
public class MethodInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Around("@annotation(noRepeatLock)")
    public Object doAround(ProceedingJoinPoint joinPoint, NoRepeatLock noRepeatLock) throws Throwable {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String paramsJsonStr = JSONObject.toJSONString(args);
        String srcStr = className + "_" + methodName + "_" + paramsJsonStr;
        String signStr = MD5Encrypt.encrypt(srcStr);

        int accessTimes = noRepeatLock.allowAccessTimes();
        long value = noRepeatLock.limitTime();

        int second = Long.valueOf(value).intValue();
        // accessTimes + "" 是 Java 中将数字转字符串的一种简便写法
        Boolean status = redisTemplate.opsForValue().setIfAbsent(signStr, accessTimes + "", Duration.ofSeconds(second));
        if (!status) {
            throw new RuntimeException("重复请求");
        }
        Object object = joinPoint.proceed();
        // 测试Aop功能
        System.out.println("方法执行了");
        return object;
    }
}
