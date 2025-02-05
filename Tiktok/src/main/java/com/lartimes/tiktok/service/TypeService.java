package com.lartimes.tiktok.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lartimes.tiktok.model.video.Type;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lartimes
 * @since 2024-12-02
 */
public interface TypeService extends IService<Type> {


    /**
     * 对多个分类进行订阅
     * @param typeIds
     * @param userId
     */
    boolean subscribeTypes(List<Long> typeIds ,Long userId);


    /**
     * 根据userId 获取关注分类
     * @param userId
     * @return
     */
    List<Type> getSubscribes(Long userId);

    /**
     * 根据userId 获取未关注分类
     * @param userId
     * @return
     */
    List<Type> getNoSubscribes(Long userId);


    List<String> random10Labels();
}
