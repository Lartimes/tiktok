package com.lartimes.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.tiktok.constant.RedisConstant;
import com.lartimes.tiktok.exception.BaseException;
import com.lartimes.tiktok.mapper.FollowMapper;
import com.lartimes.tiktok.model.po.Follow;
import com.lartimes.tiktok.model.vo.PageVo;
import com.lartimes.tiktok.service.FollowService;
import com.lartimes.tiktok.util.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lartimes
 */
@Slf4j
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {
    private static final Logger LOG = LogManager.getLogger(FollowServiceImpl.class);
    private final RedisCacheUtil redisCacheUtil;

    public FollowServiceImpl(RedisCacheUtil redisCacheUtil) {
        this.redisCacheUtil = redisCacheUtil;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean follow(Long userId, Long followUserId) {
        if (ObjectUtils.nullSafeEquals(userId, followUserId)) {
            LOG.info("不能关注自己");
            throw new BaseException("不能关注自己");
        }
        Follow follow = new Follow();
        follow.setUserId(userId);
        follow.setFollowId(followUserId);
        follow.setGmtCreated(LocalDateTime.now());
        try {
            save(follow);
            Boolean add1 = redisCacheUtil.addZSetWithScores(RedisConstant.USER_FOLLOW + userId, followUserId, null);
            Boolean add2 = redisCacheUtil.addZSetWithScores(RedisConstant.USER_FANS + followUserId, userId, null);
            return add1 && add2;
        } catch (Exception ignore) {
            boolean remove = remove(new LambdaQueryWrapper<Follow>().allEq(
                    Map.of(Follow::getFollowId, followUserId,
                            Follow::getUserId, userId)));
            LOG.info("取关 : {}", remove);
            redisCacheUtil.removeZSetValue(RedisConstant.USER_FOLLOW + userId, followUserId);
            redisCacheUtil.removeZSetValue(RedisConstant.USER_FANS + followUserId, userId);
//TODO
//            final List<Long> videoIds = (List<Long>) videoService.listVideoIdByUserId(followsId);
//            feedService.deleteInBoxFeed(userId, videoIds);
            return Boolean.FALSE;
        }
    }


    @Override
    public Long getFansCount(Long userId) {
        return count(new LambdaQueryWrapper<Follow>().eq(Follow::getFollowId, userId));
    }

    @Override
    public Collection<Long> getFansCollection(Long userId, PageVo pageVo) {
        if (pageVo == null) {
            Collection<Object> fans = redisCacheUtil.getZSetByKey(RedisConstant.USER_FANS + userId);
            fans.forEach(System.out::println);
            if (fans.isEmpty()) {
                return Collections.emptyList();
            }
            return fans.stream().map(o -> Long.valueOf(o.toString())).collect(Collectors.toList());
        }
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisCacheUtil.zSetGetByPage(RedisConstant.USER_FANS + userId, pageVo.getPage(), pageVo.getLimit());
        if (typedTuples != null) {
            if (!typedTuples.isEmpty()) {
                return typedTuples.stream().map(o -> Long.valueOf(o.getValue().toString())).collect(Collectors.toList());
            }
        }
//        db
        List<Follow> records = page(pageVo.page(), new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowId, userId)
                .orderByDesc(Follow::getGmtCreated)).getRecords();
        if (ObjectUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        return records.stream().map(record -> Long.valueOf(record.getUserId().toString())).collect(Collectors.toList());
    }

    @Override
    public Collection<Long> getFollowsCollection(Long userId, PageVo pageVo) {
        if (pageVo == null) {
            Collection<Object> fans = redisCacheUtil.getZSetByKey(RedisConstant.USER_FOLLOW + userId);
            if (fans.isEmpty()) {
                return Collections.emptyList();
            }
            return fans.stream().map(o -> Long.valueOf(o.toString())).collect(Collectors.toList());
        }
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisCacheUtil.zSetGetByPage(RedisConstant.USER_FOLLOW + userId, pageVo.getPage(), pageVo.getLimit());
        if (typedTuples != null) {
            if (!typedTuples.isEmpty()) {
                return typedTuples.stream().map(o -> Long.valueOf(o.getValue().toString())).collect(Collectors.toList());
            }
        }
//        db
        List<Follow> records = page(pageVo.page(), new LambdaQueryWrapper<Follow>()
                .eq(Follow::getUserId, userId)
                .orderByDesc(Follow::getGmtCreated)).getRecords();
        if (ObjectUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        return records.stream().map(record -> Long.valueOf(record.getFollowId().toString())).collect(Collectors.toList());
    }
}
