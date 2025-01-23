package com.lartimes.tiktok.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lartimes.tiktok.model.video.Video;
import com.lartimes.tiktok.model.vo.PageVo;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lartimes
 * @since 2024-12-02
 */
public interface VideoService extends IService<Video> {

    IPage<Video> getVideoByUserId(PageVo pageVo , Long userId);
    /**
     * 根据ID获取 视频
     * @param videoIds
     * @return
     */
    Collection<Video> getVideosByIds(List<Long> videoIds);

    /**
     * 发布/修改视频 ，
     * @param video
     */
    void postVideo(Video video);


    /**
     *  public boolean getAuditQueueState() {
     *         return executor.getTaskCount() < maximumPoolSize;
     *     }
     * @Link VideoPublishAuditService
     * @return
     */
    boolean getQueueState();



}
