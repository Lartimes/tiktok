package com.lartimes.tiktok.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lartimes.tiktok.model.po.Video;

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
    Collection<Video> getVideosByIds(List<Long> videoIds);
}
