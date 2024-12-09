package com.lartimes.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.tiktok.exception.BaseException;
import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.mapper.FavoritesMapper;
import com.lartimes.tiktok.model.po.Favorites;
import com.lartimes.tiktok.model.po.FavoritesVideo;
import com.lartimes.tiktok.model.vo.FavoritesVo;
import com.lartimes.tiktok.service.FavoritesService;
import com.lartimes.tiktok.service.FavoritesVideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lartimes
 */
@Slf4j
@Service
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites> implements FavoritesService {
    private static final Logger LOG = LogManager.getLogger(FavoritesServiceImpl.class);
    @Autowired
    private FavoritesVideoService favoritesVideoService;

    @Override
    public List<Favorites> getFavoritesByUserId(Long userId) {
        return list(new LambdaQueryWrapper<Favorites>().eq(Favorites::getUserId, userId));

    }

    @Override
    public Favorites getFavoriteById(Long userId, Long id) {
        return getOne(new LambdaQueryWrapper<Favorites>().eq(Favorites::getUserId, userId).eq(Favorites::getId, id));
    }

    @Transactional
    @Override
    public Boolean deleteFavorites(List<Long> idList) {
        boolean removed = removeBatchByIds(idList);
        LOG.info("进行删除收藏夹 : {} ,result :  {}", idList, removed);
        Long userId = UserHolder.get();
        LOG.info("删除UserID : {}", userId);
        boolean removedVideo = favoritesVideoService.remove(new LambdaQueryWrapper<FavoritesVideo>()
                .eq(Objects.nonNull(userId), FavoritesVideo::getUserId, userId)
                .in(FavoritesVideo::getFavoritesId, idList));
        LOG.info("UserID :{} 收藏夹:{}视频删除 result :{}", userId, idList, removedVideo);
        return removed;
    }

    @Transactional
    @Override
    public boolean changgeFavorites(FavoritesVo favoritesVo, Long userId) {
        if (userId == null) {
            throw new BaseException("请重新登陆");
        }
        Favorites favorites = new Favorites();
        BeanUtils.copyProperties(favoritesVo, favorites);
        try {
            LOG.info("进行新增/更新收藏夹 :{}", favorites);
            favorites.setGmtUpdated(LocalDateTime.now());
            if (favorites.getId() == null) {
                favorites.setUserId(userId);
                favorites.setGmtCreated(LocalDateTime.now());
                return this.getBaseMapper().insert(favorites) == 1;
            }
            return updateById(favorites);
        } catch (Exception e) {
            return false;
        }
    }
}
