package com.appsinnova.sdk.aop;

import com.appsinnova.sdk.dao.RedisCommonDao;
import com.appsinnova.sdk.utils.JacksonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.lang.reflect.Type;

/**
 * 说明:自定义缓存切面
 *
 * @author IGG zhaoyg
 * @date 18:59 2020/6/4
 */
@Aspect
@Component
@Slf4j
@SuppressWarnings("all")
public class AppsinnovaCacheAop {

    @Resource
    RedisCommonDao redisCommonDao;

    @Around("@annotation(appsinnovaCache)")
    public Object authentication(final ProceedingJoinPoint jp, AppsinnovaCache appsinnovaCache) throws Throwable {
        return this.aroundCacheMethod(jp, appsinnovaCache);
    }

    private Object aroundCacheMethod(ProceedingJoinPoint jp, AppsinnovaCache appsinnovaCache) throws Throwable {
        log.info("key:{},expire:{},param:{},desc:{}", appsinnovaCache.key(), appsinnovaCache.expire(), appsinnovaCache.param(), appsinnovaCache.desc());
        Object[] args = jp.getArgs();
        String cacheKey = appsinnovaCache.key();
        try {
            if (appsinnovaCache.param()) {
                cacheKey += StringUtils.join(args, "_");
            }
            String cacheValue = redisCommonDao.get(cacheKey);
            if (StringUtils.isBlank(cacheValue)) {
                //缓存找不到则穿透，获取后并写入redis
                Object result = jp.proceed();
                redisCommonDao.setEx(cacheKey, JacksonUtils.toJsonString(result), appsinnovaCache.expire());
                return result;
            }
            //找到缓存
            log.info("cacheKey:{},cacheObject:{}", cacheKey, cacheValue);
            MethodSignature methodSignature = (MethodSignature) jp.getSignature();
            return JacksonUtils.toObject(cacheValue, new TypeReference<Object>() {
                @Override
                public Type getType() {
                    return methodSignature.getMethod().getGenericReturnType();
                }
            });

        } catch (Exception e) {
            log.error("e；{}", e);
            return jp.proceed();
        }
    }

}
