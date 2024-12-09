package com.lartimes.tiktok.service;

import com.qiniu.storage.model.FileInfo;

import java.io.File;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/3 10:35
 */
public interface QiNiuFileService {


    /**
     * 获取客户端上传签名
     * @return
     */
    String getToken();
    /**
     * 上传文件
     * @param file
     */
    String uploadFile(File file);

    /**
     * 删除文件
     * @param url
     */
    void deleteFile(String url);

    /**
     * 获取文件信息
     * @param url
     * @return
     */
    FileInfo getFileInfo(String url);


    /**
     * 获取图像上传Token
     * @return
     */
    String getAvatarToken();


}
