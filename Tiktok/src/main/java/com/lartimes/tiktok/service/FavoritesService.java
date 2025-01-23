package com.lartimes.tiktok.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lartimes.tiktok.model.user.Favorites;
import com.lartimes.tiktok.model.vo.FavoritesVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lartimes
 * @since 2024-12-02
 */
public interface FavoritesService extends IService<Favorites> {


    /**
     * 获取UserId 的收藏夹
     * @param userId
     */
    List<Favorites> getFavoritesByUserId(Long userId);


    /**
     * 根据USERID id 获取收藏夹
     * @param userId
     * @param id
     * @return
     */
    Favorites getFavoriteById(Long userId, Long id);

    /**
     * 删除收藏夹以及视频
     * @param idList
     */
    Boolean deleteFavorites(List<Long> idList);

    /**
     * 添加/更改收藏夹
     * @param favoritesVo
     * @param userId
     * @return
     */
    boolean changgeFavorites(FavoritesVo favoritesVo, Long userId);
}
