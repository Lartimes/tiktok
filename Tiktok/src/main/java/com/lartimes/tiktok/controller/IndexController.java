package com.lartimes.tiktok.controller;

import com.lartimes.tiktok.model.video.Video;
import com.lartimes.tiktok.model.video.VideoShare;
import com.lartimes.tiktok.model.vo.PageVo;
import com.lartimes.tiktok.service.IndexService;
import com.lartimes.tiktok.service.VideoService;
import com.lartimes.tiktok.util.JWTUtils;
import com.lartimes.tiktok.util.R;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/8 10:06
 */
@RestController
@RequestMapping("/index")
public class IndexController {
    @Autowired
    private IndexService indexService;
    @Autowired
    private JWTUtils jWTUtils;
    @Autowired
    private VideoService videoService;


    //    参数,可能是标题,用户,YV

    /**
     * @param searchName
     * @param pageVo
     * @param request
     * @return
     * @version 1.0 //TODO 后续加入完善的搜索服务
     */
    @GetMapping("/search")
    public R searchVideo(@RequestParam(value = "searchName", required = false) String searchName,
                         PageVo pageVo,
                         HttpServletRequest request) {
//           YV , 用户 ， 标题
        return R.ok().data(videoService.searchVideo(searchName, pageVo, jWTUtils.getUserId(request)));
    }

    @PostMapping("/index/share/{videoId}")
    public R shareVideo(@PathVariable("videoId") Long videoId, HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        VideoShare videoShare = new VideoShare();
        videoShare.setVideoId(videoId);
        videoShare.setIp(ipAddress);
        if (jWTUtils.checkToken(request)) {
            videoShare.setUserId(jWTUtils.getUserId(request));
        }
        videoService.shareVideoOrUpdate(videoShare);
        return R.ok();
    }

    @GetMapping("/video/user")
    public R getUserVideo(@RequestParam(required = false) Long userId,
                          PageVo pageVo, HttpServletRequest request) {
        userId = userId == null ? jWTUtils.getUserId(request) : userId;
        return R.ok().data(videoService.getVideoByUserId(pageVo, userId));
    }

    /**
     * 获取搜索记录
     *
     * @param request
     * @return
     */
    @GetMapping("/search/history")
    public R searchHistory(HttpServletRequest request) {
        return R.ok().data(indexService.getSearchHistory(jWTUtils.getUserId(request)));
    }

    /**
     * 删除搜索记录
     *
     * @param request
     * @return
     */
    @DeleteMapping("/search/history")
    public R delSearchHistory(HttpServletRequest request) {
        return R.ok().message(indexService.delSearchHistory(jWTUtils.getUserId(request)) ?
                "删除成功" : "失败，请重试");
    }

    @GetMapping("/index/video/{videoId}")
    public R getVideoInfo(@PathVariable Long videoId, HttpServletRequest request) {
        return R.ok().data(videoService.getVideosByIds(Collections.singletonList(videoId)).stream().findFirst().get());
    }

    /**
     * 根据视频获取type
     *
     * @param id
     * @return
     */
    @RequestMapping("/video/type/{typeId}")
    public R getVideoesByFId(@PathVariable("typeId") Integer id) {
        return R.ok().data(indexService.selectVideoByTypeID(id));
    }

    /**
     * 获取所有分类
     *
     * @return
     */
    @RequestMapping("/types")
    public R getVideoTypes(HttpServletRequest request) {
        return R.ok().data(indexService.getAllTypes(request));
    }


    /**
     * 推送热门视频
     *
     * @return
     */
    @GetMapping("/video/hot")
    public R listHotVideo() {
        return R.ok().data(videoService.listHotVideo());
    }


    @GetMapping("/video/similar")
    public R pushSimilarVideo(Video video) {
        return R.ok().data(videoService.pushSimilarVideo(video));
    }


}
