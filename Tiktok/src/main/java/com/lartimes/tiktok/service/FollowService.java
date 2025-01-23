package com.lartimes.tiktok.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lartimes.tiktok.model.user.Follow;
import com.lartimes.tiktok.model.vo.PageVo;

import java.util.Collection;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lartimes
 * @since 2024-12-02
 */
public interface FollowService extends IService<Follow> {

    /**
     *进行关注/取关操作
     * @param userId 自己ID
     * @param followUserId  关注ID
     * @return
     */
    Boolean follow(Long userId, Long followUserId);


    /**
     * 获取粉丝数量
     * @param userId
     * @return
     */
    Long getFansCount(Long userId);

    /**
     * 获取粉丝Collections
     * @param userId
     * @param pageVo
     * @return
     */
    Collection<Long> getFansCollection(Long userId , PageVo pageVo);

    /**
     * 获取关注Collection
     * @param userId
     * @param pageVo
     * @return
     */
    Collection<Long> getFollowsCollection(Long userId, PageVo pageVo);
}
