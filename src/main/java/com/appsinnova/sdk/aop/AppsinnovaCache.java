package com.appsinnova.sdk.aop;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Order(Ordered.HIGHEST_PRECEDENCE)

@SuppressWarnings("all")
public @interface AppsinnovaCache {

    String desc() default "";     //描述
    long expire() default 60;     //缓存过期时间 单位:秒
    String key();
    boolean param() default false;   //缓存key是否需要拼凑参数
}
