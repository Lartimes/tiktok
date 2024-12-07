package com.lartimes.tiktok.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
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
     * 获取Collection 对象 按关注事件倒叙
     *
     * @param key
     * @return
     */
    public Collection<Object> getZSetByKey(String key) {
        return redisTemplate.opsForZSet().reverseRange(key, 0, -1);
    }

    public Set<ZSetOperations.TypedTuple<Object>> zSetGetByPage(String key, long pageNum, long pageSize) {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                long start = (pageNum - 1) * pageSize;
                long end = pageNum * pageSize - 1;
                Long size = redisTemplate.opsForZSet().size(key);
                return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, Math.min(size, end));
            }
            return null;
        } catch (Exception ignore) {
            return null;
        }


    }


    /**
     * 删除ZSET value
     *
     * @param key
     * @param value
     */
    public void removeZSetValue(String key, Long value) {
        redisTemplate.opsForZSet().remove(key, value);
    }

    /**
     * 添加ZSET
     *
     * @param key
     * @param userId
     * @param score
     * @return
     */
    public Boolean addZSetWithScores(String key, Long userId, Double score) {

        try {
            Date date = new Date();
            if (score == null) {
                score = (double) date.getTime();
            }
            return redisTemplate.opsForZSet().add(key, userId, score);
        } catch (Exception e) {
            return false;
        }
    }


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
