package com.spring.redis.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisValueCache {
    private final ValueOperations<String, Object> valueOps;

    public RedisValueCache(final RedisTemplate<String, Object> redisTemplate) {
        valueOps = redisTemplate.opsForValue();
    }

    public void cache(final String key, final Object data) {
        valueOps.set(key, data);

        //set 한 후에 특정 시간에만 데이터가 cache 되도록 할수 있다.
//        valueOps.set(key,data,40000, TimeUnit.MILLISECONDS);
    }

    public Object getCachedValue(final String key) {
        return valueOps.get(key);
    }

    public void deleteCachedValue(final String key) {
        valueOps.getOperations().delete(key);
    }
}
