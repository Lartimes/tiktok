package com.lartimes.tiktok.controller;

import com.lartimes.tiktok.service.IndexService;
import com.lartimes.tiktok.util.JWTUtils;
import com.lartimes.tiktok.util.R;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @GetMapping("/search/history")
    public R searchHistory(HttpServletRequest request) {
        return R.ok().data(indexService.getSearchHistory(jWTUtils.getUserId(request)));
    }

    @DeleteMapping("/search/history")
    public R delSearchHistory(HttpServletRequest request) {
        return R.ok().message(indexService.delSearchHistory(jWTUtils.getUserId(request)) ?
                "删除成功" : "失败，请重试");
    }


}
