package com.lartimes.tiktok.service;

import java.util.Collection;
import java.util.List;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/2/3 21:15
 */
public interface InterestPushService {

    /**
     * 根据标签获取相似视频
     *
     * @param labelNames
     * @return
     */
    Collection<Long> listVideoIdByLabels(List<String> labelNames);
}