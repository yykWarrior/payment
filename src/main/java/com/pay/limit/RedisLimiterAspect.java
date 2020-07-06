package com.pay.limit;

import com.google.common.collect.Lists;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @version v1.0
 * @ClassName: RedisLimiterAspect
 * @Description: TODO 通过注解实现redis+lua的限流，通过令牌法
 * @Author: yyk
 * @Date: 2020/6/28 8:50
 */
@Aspect
@Component
public class RedisLimiterAspect {
 /*   @Autowired
    private HttpServletResponse response;*/

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private DefaultRedisScript redisScript;

    @PostConstruct
    public void init(){
        redisScript=new DefaultRedisScript<List>();
        redisScript.setResultType(List.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit.lua")));
    }

    @Pointcut("execution(public * com.pay.controller.*.*(..))")
    public void pointcut(){
    }

    @Around("pointcut()")
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature =(MethodSignature) proceedingJoinPoint.getSignature();
        RedisLimiter declaredAnnotation = signature.getMethod().getDeclaredAnnotation(RedisLimiter.class);
        if(declaredAnnotation==null){
            return proceedingJoinPoint.proceed();
        }
        double value = declaredAnnotation.value();
        String key="ip:"+System.currentTimeMillis()/1000;
        ArrayList<String> strings = Lists.newArrayList(key);
        //用map设置lua的argv
        ArrayList<String> strings1 = Lists.newArrayList(String.valueOf(value));
        //调用脚本并执行
        List execute = (List) stringRedisTemplate.execute(redisScript, strings, String.valueOf(value));
        System.out.println("脚本执行结果"+execute);
        if("0".equals(execute.get(0).toString())){
            fullback();
            return null;
        }
        //获取令牌，继续执行
        return proceedingJoinPoint.proceed();


    }

    private void fullback() {
        System.out.println("服务器开小差，请再试一次");
    }
}
