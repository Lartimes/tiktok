package com.lartimes.tiktok.service;

import java.util.Collection;

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
}
