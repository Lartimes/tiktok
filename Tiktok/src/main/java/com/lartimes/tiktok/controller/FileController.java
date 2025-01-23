package com.lartimes.tiktok.controller;

import com.lartimes.tiktok.config.LocalCache;
import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.model.video.File;
import com.lartimes.tiktok.model.SysSetting;
import com.lartimes.tiktok.service.FileService;
import com.lartimes.tiktok.service.QiNiuFileService;
import com.lartimes.tiktok.service.SysSettingService;
import com.lartimes.tiktok.util.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/3 10:36
 */
@RestController
@RequestMapping("/file")
public class FileController implements InitializingBean {
    private static final Logger LOG = LogManager.getLogger(FileController.class);
    private final QiNiuFileService qiNiuFileService;


    private final FileService fileService;
    private final SysSettingService settingService;


    public FileController(QiNiuFileService qiNiuFileService, FileService fileService, SysSettingService settingService) {
        this.qiNiuFileService = qiNiuFileService;
        this.fileService = fileService;
        this.settingService = settingService;
    }

    /**
     * 前端获取视频/图片上传的token
     *
     * @return
     */
    @GetMapping("/getToken")
    public R getToken(@RequestParam(value = "type", required = false) String type) {
        String token = qiNiuFileService.getToken(type);
        return R.ok().data(token);
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

    /**
     * 确定是refer 指定ip  一致，
     *
     * @param request
     * @param response
     * @param fileId
     * @throws IOException
     */
    @GetMapping("/{fileId}")
    public void getUUid(HttpServletRequest request, HttpServletResponse response,
                        @PathVariable Long fileId) throws IOException {

        String ip = request.getHeader("referer");
        if (!LocalCache.containsKey(ip)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        LOG.info("refer : {}", ip);
        File url = fileService.getFileTrustUrl(fileId);
        response.setContentType(url.getFormat());
        response.sendRedirect(url.getFileKey());
    }

    @PostMapping("/auth")
    public void auth(@RequestParam(required = false) String uuid,
                     HttpServletResponse response) throws IOException {
        if (uuid == null || !LocalCache.containsKey(uuid)) {
            response.sendError(401);
        } else {
            LocalCache.rem(uuid);
            response.sendError(200);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final SysSetting setting = settingService.list().get(0);
        for (String s : setting.getAllowIp().split(",")) {
            LocalCache.put(s, true);
        }
    }

}
