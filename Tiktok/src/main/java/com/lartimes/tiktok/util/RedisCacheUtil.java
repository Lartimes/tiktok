package com.lartimes.tiktok.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/2 13:19
 */
@Component
public class RedisCacheUtil {


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 普通缓存
     *
     * @param key
     * @param value
     * @return SUCCESS
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 是否存在普通的key
     *
     * @param key
     * @return
     */
    public boolean hashKey(String key) {
        try {
            return redisTemplate.opsForValue().get(key) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 对key进行有效赋值，不赋值
     *
     * @param key
     * @param time
     * @return
     */
    public boolean expireBySeconds(String key, long time) {
        try {
            if (Boolean.TRUE.equals(redisTemplate.expire(key, time, TimeUnit.SECONDS))) {
                return true;
            }
            return this.set(key, null, time);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean set(String key, Object value, long time) {
        try {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public String getKey(String email) {
        String result = String.valueOf(redisTemplate.opsForValue().get(email));
        return result;
    }

    public void deleteKey(String email) {
        redisTemplate.delete(email);
    }
}
