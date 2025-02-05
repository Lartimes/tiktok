package com.lartimes.tiktok.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lartimes.tiktok.constant.RedisConstant;
import com.lartimes.tiktok.service.FeedService;
import com.lartimes.tiktok.util.RedisCacheUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/2/5 16:18
 */
@Service
public class FeedServiceImpl implements FeedService {
    private static final Logger LOG = LogManager.getLogger(FeedServiceImpl.class);
    private static final Long DAYS7_GAP = 1000 * 60 * 60 * 24 * 7L;
    private final RedisCacheUtil redisCacheUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FeedServiceImpl(RedisCacheUtil redisCacheUtil) {
        this.redisCacheUtil = redisCacheUtil;
    }

    @Override
    public void pusOutBoxFeed(Long userId, Long videoId, Long time) {
        redisCacheUtil.addZSetWithScores(RedisConstant.OUT_FOLLOW + userId, videoId, Double.valueOf(time));
        redisCacheUtil.expireBySeconds(RedisConstant.OUT_FOLLOW + userId, -1);
    }

    @Override
    public void pushInBoxFeed(Long userId, Long videoId, Long time) {
        //不需要推送， 只适合主动拉取
    }

    @Override
    public void deleteOutBoxFeed(Long userId, Collection<Long> fans, Long videoId) {
//        删除粉丝inbox collection
//        删除该用户outbox  single
        redisCacheUtil.getRedisTemplate().executePipelined((RedisCallback<?>) connection -> {
            for (Long fan : fans) {
                connection.zSetCommands().zRem((RedisConstant.IN_FOLLOW + fan).getBytes(), String.valueOf(videoId).getBytes());
            }
            connection.zSetCommands().zRem((RedisConstant.OUT_FOLLOW + userId).getBytes(), String.valueOf(videoId).getBytes());
            return null;
        });

    }

    @Override
    public void deleteInBoxFeed(Long userId, List<Long> videoIds) {
        redisCacheUtil.getRedisTemplate().opsForZSet().remove(RedisConstant.IN_FOLLOW + userId, videoIds.toArray());
    }

    @Override
    public void initFollowFeed(Long userId, Collection<Long> followIds) {
        long current = System.currentTimeMillis();
        long limit = current - DAYS7_GAP;
        Set<ZSetOperations.TypedTuple<Object>> set = redisCacheUtil.getRedisTemplate().opsForZSet().
                rangeWithScores(RedisConstant.IN_FOLLOW + userId, -1, -1);
        if (!ObjectUtils.isEmpty(set)) {
            Double oldTime = set.iterator().next().getScore();
            init(userId, oldTime.longValue(), current, followIds);
        } else {
            init(userId, limit, current, followIds);
        }
    }

    private void init(Long userId, long limit, long current, Collection<Long> followIds) {
//        查看关注人发件箱
        List<Set<DefaultTypedTuple<Long>>> followers = redisCacheUtil.pipeline(connection -> {
            for (Long followId : followIds) {
                connection.zRevRangeByScoreWithScores((RedisConstant.OUT_FOLLOW + followId).getBytes(),
                        limit, current, 0, 50);
            }
            return null;
        });
//        推入收件箱
        if (followers.isEmpty()) {
            LOG.info("没有新视频");
            return;
        }
        LOG.info("初始化收件箱,userId : {}" , userId);
        final byte[] key = (RedisConstant.IN_FOLLOW + userId).getBytes();
        redisCacheUtil.getRedisTemplate().executePipelined((RedisCallback<?>) connection -> {
            for (Set<DefaultTypedTuple<Long>> tuples : followers) {
                if (!ObjectUtils.isEmpty(tuples)) {
                    for (DefaultTypedTuple tuple : tuples) {
                        long score = Objects.requireNonNull(tuple.getScore()).longValue();
                        final Object value = tuple.getValue();
                        try {
                            connection.zAdd(key, score, objectMapper.writeValueAsBytes(value));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            connection.keyCommands().expire(key, RedisConstant.HISTORY_TIME);
            return null;
        });


    }
}
