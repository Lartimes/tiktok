package com.lartimes.tiktok.schedule;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/24 21:45
 */
public interface VideoScheduledService {

    /**
     * 定时半个小时更新 视频start 信息，
     * 包含: 删除点赞， 新增点赞
     */
    void updateVideoStar();


    /**
     * 热度排行榜schedule
     */
    void hotRankTopN();

    /**
     * 热门视频
     */
    void hotVideo();

    /**
     * 将 LocalDateTime 转换为毫秒数
     */
    default long toEpochMilli(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return zonedDateTime.toInstant().toEpochMilli();
    }


}
