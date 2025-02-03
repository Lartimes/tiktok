package com.lartimes.tiktok.controller;

import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.model.video.Video;
import com.lartimes.tiktok.model.vo.PageVo;
import com.lartimes.tiktok.service.FavoritesVideoService;
import com.lartimes.tiktok.service.VideoService;
import com.lartimes.tiktok.util.R;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author wüsch
 * @version 1.0
 * @description: 收藏夹---> commit
 * / -- ?
 * @since 2024/12/15 17:40
 */
@RestController
@RequestMapping("/video")
public class VideoController {
    private static final Logger LOG = LogManager.getLogger(VideoController.class);

    @Autowired
    private VideoService videoService;

    @Autowired
    private FavoritesVideoService favoritesVideoService;




    @PostMapping("/star/{videoId}")
    public R likeVideo(@PathVariable Long videoId) {
        boolean liked = videoService.likeVideo(videoId, UserHolder.get());
        return R.ok().message(liked ? "取消点赞" : "已点赞");
    }

    /**
     * 审核队列状态
     *
     * @return
     */
    @GetMapping("/audit/queue/state")
    public R getQueueState() {
        boolean queueState = videoService.getQueueState();
        R state = R.ok().state(queueState);
        state.setMessage(queueState ? "快速" : "慢速");
        return state;

    }


    /**
     * 发布/修改视频
     *
     * @param video
     * @param request
     * @return
     */
    @PostMapping
//    @Limit(limit = 5,time = 3600L,msg = "发布视频一小时内不可超过5次")
    public R postVideo(@RequestBody @Validated Video video, HttpServletRequest request) {

        videoService.postVideo(video);
        return R.ok().message("请等待审核");
    }


    /**
     * 稿件管理
     *
     * @param pageVo
     * @return
     */
    @GetMapping
    public R getUserVideo(PageVo pageVo) {
        Long userId = UserHolder.get();
        return R.ok().data(videoService.getAllVideoByUser(pageVo, userId));
    }


    /**
     * 获取收藏夹下的所有视频
     *
     * @param favoritesId
     * @return
     */
    @RequestMapping("/favorites/{favoritesId}")
    public R getFavoriteVideos(@PathVariable String favoritesId) {
        return R.ok().data(favoritesVideoService.getFavoritesVideo(Long.parseLong(favoritesId), UserHolder.get()));
    }

    /**
     * 删除视频
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public R deleteVideo(@PathVariable Long id) {
        return R.ok().message(videoService.deleteVideoById(id, UserHolder.get()) ? "删除成功" : "删除失败");
    }

    /**
     * 收藏视频
     *
     * @param fId 收藏夹ID
     * @param vId
     * @return
     */
    @PostMapping("/favorites/{fId}/{vId}")
    public R addFavoriteVideo(@PathVariable String fId, @PathVariable String vId) {

        return R.ok().message(favoritesVideoService.addFavorites(fId, vId) ? "收藏成功" : "收藏失败");
    }


    @GetMapping("/follow/feed")
    private R pushFollowVideo() {

    }
}

