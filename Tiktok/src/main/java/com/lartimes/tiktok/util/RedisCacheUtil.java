package com.lartimes.tiktok.util;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/2 13:19
 */
@Component
public class RedisCacheUtil {


    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCacheUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 获取以指定前缀开头的所有键
     *
     * @param prefix 键的前缀
     * @return 匹配的键列表
     */
    public List<String> getKeysByPrefix(String prefix) {
        List<String> keys = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().match(prefix + "*").build();
        try (Cursor<byte[]> cursorObj = Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection().keyCommands().scan(scanOptions)) {
            while (cursorObj.hasNext()) {
                keys.add(Arrays.toString(cursorObj.next()));
            }
        }
        return keys;
    }


    /**
     * 获取Collection 对象 按关注事件倒叙
     *
     * @param key
     * @return
     */
    public Collection<Object> getZSetByKey(String key) {
        return redisTemplate.opsForZSet().reverseRange(key, 0, -1);
    }

    /**
     * 另一种方法：直接使用 SDIFF 命令结合 SINTERSTORE
     *
     * @param keyA 集合 A 的键
     * @param keyB 集合 B 的键
     * @param dest 目标键，用于存储结果
     * @return 结果集合
     */
    public Set<Object> differenceIntersectionAlternative(String keyA, String keyB, String dest) {
        if(Boolean.FALSE.equals(redisTemplate.hasKey(dest))){
            redisTemplate.opsForSet().intersectAndStore(keyA, keyB, dest);
            expireBySeconds(dest , 60 * 20); //20min
        }
        return redisTemplate.opsForSet().difference(keyA, dest);
    }

    public boolean isMember(String key, Long userId) {
        Double score = redisTemplate.opsForZSet().score(key, userId);
        return score != null;
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
     * 示例：添加成员到 ZSet，所有成员分数相同
     *
     * @param key     ZSet 的键
     * @param members 要添加的成员集合
     * @return 添加成功的成员数量
     */
    public Boolean addMembers(String key, Set<Object> members) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> tuples = members.stream()
                .map(member -> new DefaultTypedTuple<Object>(member, null))
                .collect(Collectors.toSet());
        Long add = zSetOps.add(key, tuples);
        if (add != null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
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
        return String.valueOf(redisTemplate.opsForValue().get(email));
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    public Long countBits(String key) {
        return redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) {
                // 执行 BITCOUNT 命令，统计整个 Bitmap 中 1 的个数
                return connection.stringCommands().bitCount(key.getBytes());
            }
        });
    }

    public void setBit(String key, long offset) {
        redisTemplate.opsForValue().setBit(key, offset, true);
    }

    public Long getBitAsLong(String key, long offset) {
        Boolean bitValue = redisTemplate.opsForValue().getBit(key, offset);
        return bitValue != null ? bitValue ? 1L : 0L : 0L;
    }

}
