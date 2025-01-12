package com.lartimes.tiktok.controller;

import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.service.FavoritesVideoService;
import com.lartimes.tiktok.util.R;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 *  收藏夹---> commit
 *    / -- ?
 * @since 2024/12/15 17:40
 */
@RestController
@RequestMapping("/video")
public class VideoController {
    private static final Logger LOG = LogManager.getLogger(VideoController.class);

    @Autowired
    private FavoritesVideoService favoritesVideoService;




    /**
     * 获取收藏夹下的所有视频
     * @param favoritesId
     * @return
     */
    @RequestMapping("/favorites/{favoritesId}")
    public R getFavoriteVideos(@PathVariable String favoritesId) {
        return R.ok().data(favoritesVideoService.getFavoritesVideo(Long.parseLong(favoritesId), UserHolder.get()));
    }

    /**
     * 收藏视频
     * @param fId 收藏夹ID
     * @param vId
     * @return
     */
    @PostMapping("/favorites/{fId}/{vId}")
    public R addFavoriteVideo(@PathVariable String fId, @PathVariable String vId) {

        return R.ok().message(favoritesVideoService.addFavorites(fId , vId ) ?  "收藏成功" : "收藏失败");
    }

}

