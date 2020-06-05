package com.appsinnova.sdk.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j

@SuppressWarnings("all")
public class RedisCommonDao {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public String get(String key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    public boolean set(String key, String value) {
        return this.redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.set(key.getBytes(), value.getBytes()));
    }

    public boolean setEx(String key, String value, long seconds) {
        return this.redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.setEx(key.getBytes(), seconds, value.getBytes()));
    }

    public boolean setNX(String key, String value) {
        return this.redisTemplate.opsForValue().setIfAbsent(key, value);
    }                              

    public boolean setExNx(String key, String value, long seconds) {
        return this.redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.set(key.getBytes(), value.getBytes(),
                Expiration.from(seconds, TimeUnit.SECONDS), RedisStringCommands.SetOption.SET_IF_ABSENT));
    }

    public boolean setTtl(String key, long seconds) {
        return this.redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    public long getTtl(String key) {
        return this.redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public boolean delete(String key) {
        return this.redisTemplate.delete(key);
    }


    public long getNextId(String key, long defaultInitId, long step) {
        return this.redisTemplate.execute((RedisCallback<Long>) connection -> {
            connection.set(key.getBytes(), Long.valueOf(defaultInitId).toString().getBytes(), Expiration.persistent(), RedisStringCommands.SetOption.SET_IF_ABSENT);
            return connection.stringCommands().incrBy(key.getBytes(), step);
        });
    }

    public void addToSet(String key, Set<String> valueSet, long seconds) {
        if (null == valueSet || valueSet.isEmpty()) {
            return;
        }
        this.redisTemplate.opsForSet().add(key, valueSet.toArray(new String[0]));

        if (seconds > 0) {
            this.setTtl(key, seconds);
        }
    }

    public void add4Set(String key, String value) {
        this.redisTemplate.opsForSet().add(key, value);
    }

    public Integer size4Set(String key) {
        try {
            return redisTemplate.opsForSet().members(key).size();
        } catch (Exception e) {
            return 0;
        }

    }

    public void removeFromSet(String key, Set<String> valueSet) {
        if (null == valueSet || valueSet.isEmpty()) {
            return;
        }
        this.redisTemplate.opsForSet().remove(key, valueSet.toArray(new String[0]));
    }

    public Set<String> listSets(String key) {
        return this.redisTemplate.opsForSet().members(key);
    }

    public String pop4Set(String key) {
        try {
            return redisTemplate.opsForSet().pop(key);
        } catch (Exception e) {
            log.error("pop4Set:", e);
            return "";
        }

    }
}

