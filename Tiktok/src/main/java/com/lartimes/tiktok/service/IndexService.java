package com.lartimes.tiktok.service;

import com.lartimes.tiktok.model.video.Type;
import com.lartimes.tiktok.model.video.Video;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.List;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/8 10:13
 */
public interface IndexService {

    /**
     * 获取用户搜索记录
     * 默认maxSize 20条
     * @param userId
     * @return
     */
    Collection<String> getSearchHistory(Long userId);

    /**
     * 删除用户搜索记录
     * @param userId
     * @return
     */
    Boolean delSearchHistory(Long userId);

    /**
     * 根据分类获取视频
     * @param id
     * @return
     */
    List<Video> selectVideoByTypeID(Integer id);


    /**
     * 获取所有类型
     * @return
     */
    List<Type> getAllTypes(HttpServletRequest request);




}
