package com.lartimes.tiktok.service;

import com.lartimes.tiktok.model.user.User;
import com.lartimes.tiktok.model.video.Video;
import com.lartimes.tiktok.model.vo.UserModel;

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

    /**
     * 初始化用户模型
     *
     * @param userId
     * @param labels
     */
    void initUserModel(Long userId, List<String> labels);

    /**
     * 用户模型修改概率 : 可分批次发送
     * 修改场景:
     * 1.观看浏览量到达总时长1/5  +1概率
     * 2.观看浏览量未到总时长1/5 -0.5概率
     * 3.点赞视频  +2概率
     * 4.收藏视频  +3概率
     */
    void updateUserModel(UserModel userModel);


    /**
     * 用于给用户推送视频 -> 兴趣推送
     * @param user 传id和sex
     * @return videoIds
     */
    Collection<Long> listVideoIdByUserModel(User user);

    /**
     *
     * @param typeId
     * @return
     */
    Collection<Long> listVideoIdByTypeId(Long typeId);

    /**
     * 删除系统视频库
     * @param destVideo
     */
    void deleteSystemStockIn(Video destVideo);

    /**
     * 删除系统分类库
     * @param destVideo
     */
    void deleteSystemTypeStockIn(Video destVideo);

    /**
     * 添加系统分类库
     * @param video
     */
    void pushSystemTypeStockIn(Video video);

    /**
     * 系统标签库
     * @param video
     */
    void pushSystemStockIn(Video video);
}