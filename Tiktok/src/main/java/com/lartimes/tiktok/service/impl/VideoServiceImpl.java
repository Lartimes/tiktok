package com.lartimes.tiktok.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.tiktok.config.LocalCache;
import com.lartimes.tiktok.config.QiNiuConfig;
import com.lartimes.tiktok.constant.AuditStatus;
import com.lartimes.tiktok.constant.RedisConstant;
import com.lartimes.tiktok.exception.BaseException;
import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.mapper.VideoMapper;
import com.lartimes.tiktok.mapper.VideoShareMapper;
import com.lartimes.tiktok.mapper.VideoStarMapper;
import com.lartimes.tiktok.model.task.VideoTask;
import com.lartimes.tiktok.model.user.User;
import com.lartimes.tiktok.model.video.File;
import com.lartimes.tiktok.model.video.Video;
import com.lartimes.tiktok.model.video.VideoShare;
import com.lartimes.tiktok.model.video.VideoStar;
import com.lartimes.tiktok.model.vo.PageVo;
import com.lartimes.tiktok.model.vo.UserVO;
import com.lartimes.tiktok.service.FileService;
import com.lartimes.tiktok.service.TypeService;
import com.lartimes.tiktok.service.UserService;
import com.lartimes.tiktok.service.VideoService;
import com.lartimes.tiktok.service.audit.VideoPublishAuditService;
import com.lartimes.tiktok.util.FileUtil;
import com.lartimes.tiktok.util.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
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
    private static final int YV_LENGTH = "YVe564449ea12741ba8e1c6fa1".length();
    private final TypeService typeServiceImpl;
    private final FileService fileService;
    private final VideoPublishAuditService videoPublishAuditService;
    private final UserService userService;
    private final VideoShareMapper videoShareMapper;
    private final VideoStarMapper videoStarMapper;
    private final RedisCacheUtil redisCacheUtil;

    public VideoServiceImpl(TypeService typeServiceImpl, FileService fileService, VideoPublishAuditService videoPublishAuditService, UserService userService, VideoShareMapper videoShareMapper, VideoStarMapper videoStarMapper, RedisCacheUtil redisCacheUtil) {
        this.typeServiceImpl = typeServiceImpl;
        this.fileService = fileService;
        this.videoPublishAuditService = videoPublishAuditService;
        this.userService = userService;
        this.videoShareMapper = videoShareMapper;
        this.videoStarMapper = videoStarMapper;
        this.redisCacheUtil = redisCacheUtil;
    }

    @Override
    public IPage<Video> getVideoByUserId(PageVo pageVo, Long userId) {
        if (userId == null) {
            return new Page<>();
        }
        IPage<Video> page = page(pageVo.page(), new LambdaQueryWrapper<Video>().eq(Video::getUserId, userId).eq(Video::getDeleted, 0).eq(Video::getAuditStatus, AuditStatus.SUCCESS).orderByDesc(Video::getGmtCreated, Video::getGmtUpdated));
        final List<Video> videos = page.getRecords();
        setUserVoAndUrl(videos);
        return page;
    }

    /**
     * 将Video 改为VideoVo
     *
     * @param videos
     */
    private void setUserVoAndUrl(List<Video> videos) {
        if (videos.isEmpty()) {
            return;
        }
        HashSet<Long> userIdSet = new HashSet<>();
        ArrayList<Long> fileIds = new ArrayList<>();
        videos.forEach(video -> {
            userIdSet.add(video.getUserId());
            fileIds.add(video.getUrl());
            fileIds.add(video.getCover());
        });
        Map<Long, File> fileMap = fileService.listByIds(fileIds).stream().collect(Collectors.toMap(File::getId, Function.identity()));
        Map<Long, User> userMap = userService.listByIds(userIdSet).stream().collect(Collectors.toMap(User::getId, Function.identity()));
        for (Video video : videos) {
            final UserVO userVO = new UserVO();
            final User user = userMap.get(video.getUserId());
            userVO.setId(video.getUserId());
            userVO.setNickName(user.getNickName());
            userVO.setDescription(user.getDescription());
            userVO.setSex(user.getSex());
            video.setUser(userVO);
            final File file = fileMap.get(video.getUrl());
            video.setVideoType(file.getFormat());
        }


    }

    @Override
    public Collection<Video> getVideosByIds(List<Long> videoIds) {
        Collection<Video> videos = list(new LambdaQueryWrapper<Video>().in(Video::getId, videoIds).eq(Video::getDeleted, 0).select()).stream().distinct().collect(Collectors.toCollection(ArrayList::new));
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
        Long priId = video.getId();
        if (priId != null) {
            oldVideo = getOne(new LambdaQueryWrapper<Video>().eq(Video::getUserId, userId).eq(Video::getId, priId));
            if (!(video.getVideoUrl()).equals(oldVideo.getVideoUrl()) || !(video.getCoverUrl().equals(oldVideo.getCoverUrl()))) {
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
        boolean added = priId == null;
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
                LOG.info("没设置封面");
                video.setCover(fileService.generatePhoto(fileId, userId));
            }
            video.setYv("YV" + UUID.randomUUID().toString().replace("-", "").substring(8));
        }
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

        videoPublishAuditService.audit(videoTask, false);
    }

    @Override
    public boolean getQueueState() {
        return this.videoPublishAuditService.getAuditQueueState();
    }

    @Override
    public IPage<Video> getAllVideoByUser(PageVo pageVo, Long userId) {
        IPage<Video> page = page(pageVo.page(), new LambdaQueryWrapper<Video>().eq(Video::getUserId, userId).orderByDesc(Video::getGmtCreated, Video::getGmtUpdated));
        setUserVoAndUrl(page.getRecords());
        return page;
    }

    @Transactional
    @Override
    public boolean deleteVideoById(Long videoId, Long userId) {
        if (videoId == null) {
            throw new BaseException("指定视频不存在");
        }
        Video destVideo = getOne(new LambdaQueryWrapper<Video>().eq(Video::getId, videoId).eq(Video::getUserId, userId));
        if (destVideo == null) {
            throw new BaseException("非本人视频");
        }
        final boolean b = removeById(videoId);
        if (b) {
            // 解耦
            new Thread(() -> {
                // 删除分享量 点赞量
//                TODO 标签兴趣推送
                videoShareMapper.delete(new LambdaQueryWrapper<VideoShare>().eq(VideoShare::getVideoId, videoId).eq(VideoShare::getUserId, userId));
                videoStarMapper.delete(new LambdaQueryWrapper<VideoStar>().eq(VideoStar::getVideoId, videoId).eq(VideoStar::getUserId, userId));
//                interestPushService.deleteSystemStockIn(destVideo);
//                interestPushService.deleteSystemTypeStockIn(destVideo);
            }).start();
        }
        return b;
    }

    @Override
    public boolean likeVideo(Long videoId, Long userId) {

        Video video = getOne(new LambdaQueryWrapper<Video>().eq(videoId != null, Video::getId, videoId));
        if (video == null) {
            throw new BaseException("该视频消失不见了");
        }
        final String likeIdStr = RedisConstant.VIDEO_LIKE_IDS + videoId;
        final String likeNumStr = RedisConstant.VIDEO_LIKE_NUM + videoId;
        boolean member = redisCacheUtil.isMember(likeIdStr, userId);
//        根据是否点赞 lua脚本原子性+ -操作
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("like_video.lua")));
        redisScript.setResultType(Long.class);
        Long result = redisCacheUtil.getRedisTemplate().execute(redisScript, Collections.emptyList(), videoId, userId);
        if (result == null || result == -1) {
            LOG.error("点赞失败，like_video.lua");
            throw new BaseException("点赞失败，请重试");
        }
//        000000000000000000010000
        Long starCounts = redisCacheUtil.countBits(likeNumStr);
        LOG.info("点赞成功 : {}", result);
        LOG.info("当前startCounts : {}", starCounts);
//        异步改变用户模型
        // 获取标签
//        TODO 更新用户模型
        new Thread(() -> {
            List<String> labels = video.buildLabel();
//            UserModel userModel = UserModel.buildUserModel(labels, videoId, 1.0);
//            interestPushService.updateUserModel(userModel);
        }).start();
//        数据分析出某个时间带你突增的GAP
//        主动进行DB写入 ?
//        两个时间段的set , 进行 3个集合的操作 ?
//        点赞 %2w 超过 之前的就存入，异步写入json ？ 其他文件  对user 进行redis 分片
//        如果redis 挂了怎么办？
//        DB操作？
// TODO 后续再说，此处直接写入
//        冷机之后进行写入
        return member;
    }

    @Override
    public void shareVideoOrUpdate(VideoShare videoShare) {
        final Video video = getById(videoShare.getVideoId());
        if (video == null) throw new BaseException("指定视频不存在");
        boolean result = doShare(videoShare);
        updateShare(video, result ? 1L : 0L);
    }

    //TODO Binlog 监听 是异步、解耦的数据同步方案，适合 MySQL 到 Elasticsearch 的实时同步。
    @Override
    public IPage<Video> searchVideo(String searchName, PageVo pageVo, Long userId) {
        IPage page = pageVo.page();
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<Video>().eq(Video::getAuditStatus, AuditStatus.SUCCESS);
        int index = searchName.indexOf("YV");
        boolean flag = false;
        if (StringUtils.hasText(searchName)) {
            searchName = searchName.trim();
            flag = true;
        }
        if (flag && index >= 0) {
            String substring = searchName.substring(index, index + YV_LENGTH);
            LOG.info("搜索YV号:{}", substring);
            queryWrapper.like(Video::getYv, substring);
        } else if (flag) {
//            TODO 分词器
            LOG.info("模糊查询:{}", searchName);
            queryWrapper.like(Video::getTitle, searchName);
        }
        IPage<Video> result = page(page, queryWrapper);
        List<Video> videoList = result.getRecords();
        setUserVoAndUrl(videoList);
        userService.addSearchHistory(userId ,searchName );
        return result;
    }

    //    行锁 最简单，效率快
    private void updateShare(Video video, long signal) {
        final UpdateWrapper<Video> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("share_count = share_count + " + signal);
        updateWrapper.lambda().eq(Video::getId, video.getId()).eq(Video::getShareCount, video.getShareCount());
        update(video, updateWrapper);
    }

    // ip + videoId 作为唯一索引 |  insert ignore
    private boolean doShare(VideoShare videoShare) {
        try {
//            Ip + videoId 作为唯一索引
            videoShareMapper.insert(videoShare);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
