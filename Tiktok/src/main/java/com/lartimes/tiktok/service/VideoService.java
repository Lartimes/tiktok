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
    /**
     * 获取用户稿件管理所有视频
     * @param pageVo
     * @param userId
     * @return
     */
    IPage<Video>  getAllVideoByUser(PageVo pageVo, Long userId);


    /**
     * 删除本人的视频
     * @param videoId
     * @param userId
     * @return
     */
    boolean deleteVideoById(Long videoId, Long userId);


    /**
     * 点赞/取消点赞
     * @param videoId
     * @param aLong
     * @return
     */
    boolean likeVideo(Long videoId, Long aLong);

}
