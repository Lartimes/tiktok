package com.lartimes.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.tiktok.config.LocalCache;
import com.lartimes.tiktok.config.QiNiuConfig;
import com.lartimes.tiktok.constant.AuditStatus;
import com.lartimes.tiktok.exception.BaseException;
import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.mapper.VideoMapper;
import com.lartimes.tiktok.model.po.File;
import com.lartimes.tiktok.model.po.Video;
import com.lartimes.tiktok.model.task.VideoTask;
import com.lartimes.tiktok.service.FileService;
import com.lartimes.tiktok.service.TypeService;
import com.lartimes.tiktok.service.VideoService;
import com.lartimes.tiktok.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lartimes
 */
@Slf4j
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {
    private static final Logger LOG = LogManager.getLogger(VideoServiceImpl.class);
    private final TypeService typeServiceImpl;
    private final FileService fileService;

    public VideoServiceImpl(TypeServiceImpl typeServiceImpl, FileService fileService) {
        this.typeServiceImpl = typeServiceImpl;
        this.fileService = fileService;
    }

    @Override
    public Collection<Video> getVideosByIds(List<Long> videoIds) {
        Collection<Video> videos = list(new LambdaQueryWrapper<Video>().in(Video::getId, videoIds)
                .select()).stream().distinct().collect(Collectors.toCollection(ArrayList::new));
        LOG.info("videos list:{}", videos);
        if (videos.isEmpty()) {
            return Collections.emptyList();
        }
        return videos;
    }

    @Override
    public void postVideo(Video video) {
//        用户信息
        Long userId = UserHolder.get();
        video.setUserId(userId);
        Video oldVideo = null;
        Long videoId = video.getId();
        if (videoId != null) {
            oldVideo = getOne(new LambdaQueryWrapper<Video>()
                    .eq(Video::getUserId, userId)
                    .eq(Video::getId, video.getId()));
            if (!(video.getVideoUrl()).equals(oldVideo.getVideoUrl()) ||
                    !(video.getCoverUrl().equals(oldVideo.getCoverUrl()))) {
                throw new BaseException("不能更换视频源,只能修改视频信息");
            }
        }
//        分类
        Long typeId = video.getTypeId();
        if (typeServiceImpl.getById(typeId) == null) {
            throw new BaseException("分类不存在");
        }
//        标签
        List<String> labels = video.buildLabel();
        if (labels.size() > 5) {
            throw new BaseException("标签最多只能选择5个");
        }
        // 修改状态
        video.setAuditStatus(AuditStatus.PROCESS);
//         是否添加
        boolean added = oldVideo == null;
        video.setYv(null);
        Long fileId = video.getUrl();
        File videoFile = fileService.getById(fileId);
        String videoUrl = QiNiuConfig.CNAME + "/" + videoFile.getFileKey();
        if (!added) {
            video.setVideoType(null);
            video.setLabelNames(null);
            video.setUrl(null);
            video.setCover(null);
        } else {

            // 如果没设置封面,我们帮他设置一个封面
            if (ObjectUtils.isEmpty(video.getCover())) {

                video.setCover(fileService.generatePhoto(videoUrl, userId));
            }

            video.setYv("YV" + UUID.randomUUID().toString().replace("-", "").substring(8));
        }
//videotask
        // 填充视频时长 (若上次发布视频不存在Duration则会尝试获取)
        if (added || !StringUtils.hasLength(oldVideo.getDuration())) {
            final String uuid = UUID.randomUUID().toString();
            LocalCache.put(uuid, true);
            try {
                LOG.info("视频URL解析: {}", videoUrl);
                final String duration = FileUtil.getVideoDuration(videoUrl + "?uuid=" + uuid);
                video.setDuration(duration);
            } finally {
                LocalCache.rem(uuid);
            }
        }
        LOG.info("视频info：{}", video);
        video.setGmtCreated(LocalDateTime.now());
        this.saveOrUpdate(video);

        final VideoTask videoTask = new VideoTask();
        videoTask.setOldVideo(video);
        videoTask.setVideo(video);
        videoTask.setIsAdd(added);
        videoTask.setOldState(added || video.getOpen());
        videoTask.setNewState(true);
        //TODO as for videos, continue to audit ? need re-arrange maybe?
        videoPublishAuditService.audit(videoTask, false);
    }

}
