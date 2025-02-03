package com.lartimes.tiktok.service.impl;

import com.lartimes.tiktok.constant.RedisConstant;
import com.lartimes.tiktok.service.InterestPushService;
import com.lartimes.tiktok.util.RedisCacheUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/2/3 21:37
 */
@Service
public class InterestPushServiceImpl implements InterestPushService {
    private static final Logger LOG = LogManager.getLogger(InterestPushServiceImpl.class);
    private final RedisCacheUtil redisCacheUtil;

    public InterestPushServiceImpl(RedisCacheUtil redisCacheUtil) {
        this.redisCacheUtil = redisCacheUtil;
    }

    @Override
    public Collection<Long> listVideoIdByLabels(List<String> labelNames) {
        if (labelNames == null || labelNames.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> arr = new ArrayList<>();
        for (String labelName : labelNames) {
            arr.add(RedisConstant.SYSTEM_STOCK + labelName);
        }
        Set<Long> videoIds = new HashSet<>();
        final Collection<Object> result = redisCacheUtil.sRandom(arr);
        result.stream().filter(Objects::nonNull).distinct().map(a -> Long.parseLong(a.toString()))
                .forEach(videoIds::add);
        return videoIds;
    }
}
