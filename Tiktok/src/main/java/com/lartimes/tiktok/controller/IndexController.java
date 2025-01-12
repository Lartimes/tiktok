package com.lartimes.tiktok.controller;

import com.lartimes.tiktok.service.IndexService;
import com.lartimes.tiktok.util.JWTUtils;
import com.lartimes.tiktok.util.R;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    /**
     *  根据视频获取type
     * @param id
     * @return
     */
    @RequestMapping("/video/type/{typeId}")
    public R getVideoesByFId(@PathVariable("typeId") Integer id) {
        return R.ok().data(indexService.selectVideoByTypeID(id));
    }

    /**
     * 获取所有分类
     * @return
     */
    @RequestMapping("/types")
    public R getVideoTypes() {
        return R.ok().data(indexService.getAllTypes());
    }


}
