package com.lartimes.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lartimes.tiktok.constant.RedisConstant;
import com.lartimes.tiktok.model.po.Type;
import com.lartimes.tiktok.model.po.Video;
import com.lartimes.tiktok.service.IndexService;
import com.lartimes.tiktok.service.TypeService;
import com.lartimes.tiktok.service.VideoService;
import com.lartimes.tiktok.util.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/8 10:14
 */
@Service
@Slf4j
public class IndexServiceImpl implements IndexService {

    private static final Logger LOG = LogManager.getLogger(IndexServiceImpl.class);
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private VideoService videoService;
    @Autowired
    private TypeService typeService;

    @Override
    public Collection<String> getSearchHistory(Long userId) {
        if (userId != null) {
            Collection<Object> histories = redisCacheUtil.getZSetByKey(RedisConstant.USER_SEARCH_HISTORY + userId);
            if (histories.isEmpty()) {
                return Collections.emptyList();
            }
            return histories.stream().distinct().map(String::valueOf).toList()
                    .subList(0, Math.min(histories.size(), 20));
        }
        return Collections.emptyList();
    }

    @Override
    public Boolean delSearchHistory(Long userId) {
        try {
            redisCacheUtil.deleteKey(RedisConstant.USER_SEARCH_HISTORY + userId);
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    @Override
    public List<Video> selectVideoByTypeID(Integer id) {
        List<Video> videos = videoService.getBaseMapper().selectList(new LambdaQueryWrapper<Video>()
                .eq(Video::getTypeId, id));
        if (videos == null || videos.isEmpty()) {
            return Collections.emptyList();
        }
        return videos;
    }

    @Override
    public List<Type> getAllTypes() {
        return typeService.getBaseMapper().selectList(null);
    }


}
