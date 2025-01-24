package com.lartimes.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.tiktok.exception.BaseException;
import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.mapper.FavoritesVideoMapper;
import com.lartimes.tiktok.model.user.Favorites;
import com.lartimes.tiktok.model.user.FavoritesVideo;
import com.lartimes.tiktok.model.video.Video;
import com.lartimes.tiktok.service.FavoritesVideoService;
import com.lartimes.tiktok.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lartimes
 */
@Slf4j
@Service
public class FavoritesVideoServiceImpl extends ServiceImpl<FavoritesVideoMapper, FavoritesVideo> implements FavoritesVideoService {
    private static final Logger LOG = LogManager.getLogger(FavoritesVideoServiceImpl.class);

    private final FavoritesServiceImpl favoritesService;
    private final VideoService videoService;

    public FavoritesVideoServiceImpl(FavoritesServiceImpl favoritesService, VideoService videoService) {
        this.favoritesService = favoritesService;
        this.videoService = videoService;
    }

    @Override
    public Collection<Video> getFavoritesVideo(Long favoritesId, Long userId) {
        List<Long> longList = list(new LambdaQueryWrapper<FavoritesVideo>()
                .eq(FavoritesVideo::getFavoritesId, favoritesId)
                .eq(FavoritesVideo::getUserId, userId)
                .select(FavoritesVideo::getVideoId))
                .stream().map(FavoritesVideo::getVideoId).toList();
        if (longList.isEmpty()) {
            LOG.info("该收藏夹为空");
            throw new BaseException("该收藏夹为空");
        }

        return videoService.getVideosByIds(longList);
    }

    @Override
    public boolean addFavorites(String fId, String vId) {
        Long userId = UserHolder.get();
        long count = favoritesService.count(
                new LambdaQueryWrapper<Favorites>().eq(Favorites::getUserId, userId));
        //        是否存在该收藏夹
        if (count <= 0) {
//            boolean b = favoritesService.changgeFavorites(new FavoritesVo(), userId);
            throw new BaseException("收藏夹不存在，请重新创建");
        }
        FavoritesVideo favoritesVideo = new FavoritesVideo();
        favoritesVideo.setFavoritesId(Long.parseLong(fId));
        favoritesVideo.setUserId(userId);
        favoritesVideo.setVideoId(Long.parseLong(vId));
        int insert = this.baseMapper.insert(favoritesVideo);
        if (insert >= 1) {
            LOG.info("收藏成功 , : {}", favoritesVideo);
            return true;
        }
        throw new BaseException("收藏失败,请重试");
    }


}
