package com.lartimes.tiktok.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lartimes.tiktok.model.po.FavoritesVideo;
import com.lartimes.tiktok.model.po.Video;

import java.util.Collection;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lartimes
 * @since 2024-12-02
 */
public interface FavoritesVideoService extends IService<FavoritesVideo> {

    Collection<Video> getFavoritesVideo(Long favoritesId, Long userId);

    /**
     *
     * @param fId
     * @param vId
     * @return
     */
    boolean addFavorites(String fId, String vId);
}
