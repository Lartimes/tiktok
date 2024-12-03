package com.lartimes.tiktok.controller;

import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.service.FileService;
import com.lartimes.tiktok.service.QiNiuFileService;
import com.lartimes.tiktok.util.R;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/3 10:36
 */
@RestController
@RequestMapping("/file")
public class FileController {
    private static final Logger LOG = LogManager.getLogger(FileController.class);
    @Autowired
    private QiNiuFileService qiNiuFileService;


    @Autowired
    private FileService fileService;

    @GetMapping("/getToken")
    public R getToken() {
        return R.ok()
                .data(qiNiuFileService.getToken());
    }


    /**
     * 保存到文件表
     *
     * @return
     */
    @PostMapping
    public R save(String fileKey) {
        return R.ok().data(fileService.save(fileKey, UserHolder.get()));
    }




}
