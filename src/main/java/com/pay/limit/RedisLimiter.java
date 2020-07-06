package com.pay.limit;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLimiter {
    @AliasFor("limiter")
    double value() default Double.MAX_VALUE;
    double limit() default Double.MAX_VALUE;
}
