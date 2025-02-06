package com.lartimes.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.tiktok.config.LocalCache;
import com.lartimes.tiktok.exception.BaseException;
import com.lartimes.tiktok.mapper.FileMapper;
import com.lartimes.tiktok.model.video.File;
import com.lartimes.tiktok.service.FileService;
import com.lartimes.tiktok.service.QiNiuFileService;
import com.qiniu.storage.model.FileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lartimes
 */
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

    @Override
    public File getFileTrustUrl(Long fileId) {
        File file = getById(fileId);
        if (Objects.isNull(file)) {
            throw new BaseException("未找到该文件");
        }
        final String s = UUID.randomUUID().toString();
        //双重LocalCache
        LocalCache.put(s, true);
        String url = qiNiuFileService.getCname() + "/" + file.getFileKey();

        if (url.contains("?")) {
            url = url + "&uuid=" + s;
        } else {
            url = url + "?uuid=" + s;
        }
        file.setFileKey(url);
        return file;
    }

    @Transactional
    @Override
    public Long generatePhoto(Long fileId, Long userId) {
        final File file = getById(fileId);
        final String fileKey = file.getFileKey() + "?vframe/jpg/offset/1";
        final File fileInfo = new File();
        fileInfo.setFileKey(fileKey);
        fileInfo.setFormat("image/*");
        fileInfo.setType("图片");
        fileInfo.setUserId(userId);
        save(fileInfo);
        return fileInfo.getId();
    }
}
