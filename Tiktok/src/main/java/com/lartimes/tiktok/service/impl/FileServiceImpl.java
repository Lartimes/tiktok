package com.lartimes.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.tiktok.mapper.FileMapper;
import com.lartimes.tiktok.model.po.File;
import com.lartimes.tiktok.service.FileService;
import com.lartimes.tiktok.service.QiNiuFileService;
import com.qiniu.storage.model.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lartimes
 */
@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
    private static final Logger LOG = LogManager.getLogger(FileServiceImpl.class);
    @Autowired
    private QiNiuFileService qiNiuFileService;

    @Transactional
    @Override
    public Long save(String fileKey, Long userId) {
        LOG.info("fileKey  ： {}", fileKey);
        final FileInfo fileInfo = qiNiuFileService.getFileInfo(fileKey);
        if (ObjectUtils.isEmpty(fileInfo)) {
            throw new IllegalArgumentException("参数不正确");
        }
        File file = new File();
        file.setFileKey(fileKey);
        file.setSize(fileInfo.fsize);
        file.setUserId(userId);
        final String mimeType = fileInfo.mimeType;
        file.setFormat(mimeType);
        file.setType(mimeType.contains("video") ? "视频" : "图片");
        file.setGmtUpdated(LocalDateTime.now());
        file.setGmtCreated(LocalDateTime.now());
        File one = this.getOne(new LambdaQueryWrapper<File>()
                .eq(Objects.nonNull(fileKey), File::getFileKey, fileKey));
        if (!ObjectUtils.isEmpty(one)) {
            LOG.info("已经存在该文件:{}", one);
            return one.getId();
        }
        this.save(file);
        LOG.info("保存文件信息:{}", file);
        return file.getId();
    }
}
