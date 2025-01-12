package com.lartimes.tiktok.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lartimes.tiktok.model.po.File;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lartimes
 * @since 2024-12-02
 */
public interface FileService extends IService<File> {
    /**
     * 保存本地文件
     * @param fileKey
     * @param userId
     * @return
     */
    Long save(String fileKey,Long userId);

    /**
     * 根据FileId 返回File
     * @param fileId
     * @return
     */
    File getFileTrustUrl(Long fileId);

}
