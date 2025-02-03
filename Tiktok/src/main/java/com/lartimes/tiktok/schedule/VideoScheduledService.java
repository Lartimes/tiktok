package com.lartimes.tiktok.schedule;

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


}
