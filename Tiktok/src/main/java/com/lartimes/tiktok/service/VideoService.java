package com.lartimes.tiktok.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lartimes.tiktok.model.video.Video;
import com.lartimes.tiktok.model.video.VideoShare;
import com.lartimes.tiktok.model.vo.HotVideo;
import com.lartimes.tiktok.model.vo.PageVo;

import java.util.Collection;
import java.util.LinkedHashMap;
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

    /**
     * 更新分享视频/修改时间
     * @param videoShare
     */
    void shareVideoOrUpdate(VideoShare videoShare);

    /**
     * 搜索视频 用户？ 标题 ？ YV号
     * @param searchName
     * @param pageVo
     * @param userId
     * @return
     */
    IPage<Video> searchVideo(String searchName, PageVo pageVo, Long userId);


    /**
     * 推送热门视频
     * @return
     */
    Collection<Video>  listHotVideo();


    /**
     * 根据标签推送相似视频
     * @param video
     * @return
     */
    Collection<Video> pushSimilarVideo(Video video);


    /**
     * 推送关注人的视频
     * @param userId
     * @param lastTime
     * @return
     */
    Collection<Video> followFeed(Long userId, Long lastTime);

    /**
     * 初始化收件箱
     * @param userId
     */
    void initFollowFeed(Long userId);


    /**
     * hotrank 热度排行榜
     * @return
     */
    Collection<HotVideo> hotRank();


    /**
     *
     * @param userId
     * @return
     */
    Collection<Video> pushVideos(Long userId);

    /**
     * 根据视频分类获取视频
     * @param typeId
     * @return
     */
    Collection<Video> getVideoByTypeId(Long typeId);

    /**
     * 添加视频浏览记录
     * @param videoID
     * @param userId
     */
    void historyVideo(Long videoID, Long userId);


    /**
     *获取浏览记录 ， 日期 ， ---- video
     * @param pageVo
     * @return
     */
    LinkedHashMap<String, List<Video>> getHistory(PageVo pageVo);

    /**
     * 获取用户个人视频ID
     * @param followUserId
     * @return
     */
    Collection<Long> listVideoIdByUserId(Long followUserId);


    Collection<Video> getFavoritesVideo(Long favoritesId, Long userId);

    /**
     *
     * @param fId
     * @param vId
     * @return
     */
    boolean addFavorites(String fId, String vId);
}
