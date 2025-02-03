package com.lartimes.tiktok.schedule.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lartimes.tiktok.constant.AuditStatus;
import com.lartimes.tiktok.constant.RedisConstant;
import com.lartimes.tiktok.exception.BaseException;
import com.lartimes.tiktok.mapper.SysSettingMapper;
import com.lartimes.tiktok.mapper.VideoMapper;
import com.lartimes.tiktok.mapper.VideoStarMapper;
import com.lartimes.tiktok.model.SysSetting;
import com.lartimes.tiktok.model.video.Video;
import com.lartimes.tiktok.model.video.VideoStar;
import com.lartimes.tiktok.model.vo.HotVideo;
import com.lartimes.tiktok.schedule.TopK;
import com.lartimes.tiktok.schedule.VideoScheduledService;
import com.lartimes.tiktok.util.RedisCacheUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/24 21:46
 */
@Service
public class VideoScheduledServiceImpl implements VideoScheduledService {
    private static final Logger LOG = LogManager.getLogger(VideoScheduledServiceImpl.class);
    private static final long PAGE_SIZE = 100L;
    private static final int BATCH_SIZE = 1000;
    static double a = 0.011;

    private final Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private VideoStarMapper videoStarMapper;
    @Autowired
    @Qualifier("batchSqlSessionFactory")
    private SqlSessionFactory batchSqlSessionFactory;
    @Autowired
    private SysSettingMapper sysSettingMapper;

    /**
     * 将 LocalDateTime 转换为毫秒数
     */
    private static long toEpochMilli(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return zonedDateTime.toInstant().toEpochMilli();
    }

    @Scheduled(cron = "0 0/30 * * * ?") // 每 30 分钟的第 0 秒执行一次
    @Override
    public void updateVideoStar() {
        try (SqlSession sqlSession = batchSqlSessionFactory.openSession()) {
//                list foreach ---> 执行？
            long now = System.currentTimeMillis();
            LOG.info("定时任务updateVideoStar(): 获取长连接connection,以及batch sqlsession");
            updateVideoStar(sqlSession);
            long then = System.currentTimeMillis();
            LOG.info("定时任务updateVideoStar(): 完成,耗时:{}秒", (then - now) / 1e3);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new BaseException(e.getMessage());
        }
    }

    //    TODO : 表的分片设计 提高效率 此处直接查询，到redis推上去就行集合的运算
    private void updateVideoStar(SqlSession sqlSession) {
//        getKeysByPrefix
//        List<String> keys = redisCacheUtil.getKeysByPrefix(RedisConstant.VIDEO_LIKE_IDS);
        Set<Long> videoIds = videoMapper.selectList(new LambdaQueryWrapper<Video>()
                .select(Video::getId)).stream().map(Video::getId).collect(Collectors.toSet());
        LOG.info("updateVideoStar(SqlSession sqlSession): 逐步写入redis db likeIDs");
        for (Long videoId : videoIds) {
//            分页返回，写入zset
            Long count = videoStarMapper.selectCount(new LambdaQueryWrapper<VideoStar>()
                    .eq(VideoStar::getVideoId, videoId));
            if (count != null && count != 0L) {
                long loops = 1L;
                long tmp = count / PAGE_SIZE;
                if (tmp > 0) {
                    loops += tmp;
                }
                for (int i = 0; i < loops; i++) {
                    Set<Object> userIds = videoStarMapper.selectPage(new Page<VideoStar>(i, PAGE_SIZE),
                            new LambdaQueryWrapper<VideoStar>()
                                    .eq(VideoStar::getVideoId, videoId)
                                    .select(VideoStar::getUserId)).getRecords().stream().map(VideoStar::getUserId).collect(Collectors.toSet());
                    redisCacheUtil.addMembers("DB:" + RedisConstant.VIDEO_LIKE_IDS + videoId, userIds);
                }
            }
        }
        LOG.info("进行集合运算，进行更新DB :  db ∩ redis");
        try {
            VideoStarMapper mapper = sqlSession.getMapper(VideoStarMapper.class);
//            两个集合进行筛选出独特的 一个remove ， 一个update
            for (Long videoId : videoIds) {
                String tmp = "diff:" + videoId;
                Set<Long> removedIds = redisCacheUtil.differenceIntersectionAlternative("DB:" + RedisConstant.VIDEO_LIKE_IDS + videoId,
                        RedisConstant.VIDEO_LIKE_IDS + videoId, tmp).stream().map((id -> Long.parseLong(id.toString()))).collect(Collectors.toSet());
                LOG.info("removedIds : {}", removedIds);
                List<VideoStar> addedStars = redisCacheUtil.differenceIntersectionAlternative(RedisConstant.VIDEO_LIKE_IDS + videoId,
                                "DB:" + RedisConstant.VIDEO_LIKE_IDS + videoId, tmp).stream()
                        .map(((id -> {
                            VideoStar videoStar = new VideoStar();
                            videoStar.setId(null);
                            videoStar.setGmtUpdated(LocalDateTime.now());
                            videoStar.setUserId(Long.parseLong(id.toString()));
                            videoStar.setVideoId(videoId);
                            return videoStar;
                        }))).toList();
                LOG.info("addedStars : {}", addedStars);
                //                db - db ∩ redis
                //                redis - db ∩ redis
                boolean isUnliked = !removedIds.isEmpty();
                if (isUnliked) {
                    mapper.delete(new LambdaQueryWrapper<VideoStar>()
                            .eq(VideoStar::getVideoId, videoId)
                            .in(VideoStar::getUserId, removedIds));
                }
                LOG.info("videoId 三十分钟内{}取消点赞:{}", isUnliked ? "有" : "无", removedIds.size());
                int size = addedStars.size();
                if (size == 0) {
                    LOG.info("videoId 三十分钟内无点赞:{}", videoId);
                    continue;
                }
                int count = 0;
                for (int i = 0; i < size; i += BATCH_SIZE) {
                    int end = Math.min(i + BATCH_SIZE, size);
                    count += mapper.insertBatchSomeColumn(addedStars.subList(i, end));
                }
                if (count == size) {
                    LOG.info("异步写入DB成功---videoId:{}", videoId);
                }
            }
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback(true);
            LOG.error(e.getMessage());
            throw new BaseException(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 */1 * * ?")
    @Override
    public void hotRankTopN() {
        // 控制数量
//        最小堆
        final TopK topK = new TopK(10, new PriorityQueue<HotVideo>(10,
                Comparator.comparing(HotVideo::getHot)));
        long limit = 1000;
        long id = 0;
        // 每次拿1000个
        List<Video> videos = videoMapper.selectList(new LambdaQueryWrapper<Video>()
                .select(Video::getId, Video::getShareCount, Video::getHistoryCount,
                        Video::getStartCount, Video::getFavoritesCount,
                        Video::getGmtCreated, Video::getTitle)
                .gt(Video::getId, id)
                .eq(Video::getAuditStatus, AuditStatus.SUCCESS)
                .eq(Video::getOpen, 0)
                .last("limit " + limit));
        while (!ObjectUtils.isEmpty(videos)) {
            for (Video video : videos) {
                Long shareCount = video.getShareCount();
                Double historyCount = video.getHistoryCount() * 0.8;
                Long startCount = video.getStartCount();
                Double favoritesCount = video.getFavoritesCount() * 1.5;
                LocalDateTime now = LocalDateTime.now();
                long t = toEpochMilli(now) - toEpochMilli(video.getGmtCreated());
//                去重 + 随机数
                final double v = weightRandom();
                final double hot = hot(shareCount + historyCount + startCount + favoritesCount + v, TimeUnit.MILLISECONDS.toDays(t));
                final HotVideo hotVideo = new HotVideo(hot, video.getId(), video.getTitle());
                topK.add(hotVideo);
            }
            id = videos.get(videos.size() - 1).getId();
            videos = videoMapper.selectList(new LambdaQueryWrapper<Video>()
                    .select(Video::getId, Video::getShareCount, Video::getHistoryCount,
                            Video::getStartCount, Video::getFavoritesCount,
                            Video::getGmtCreated, Video::getTitle)
                    .gt(Video::getId, id)
                    .eq(Video::getAuditStatus, AuditStatus.SUCCESS)
                    .eq(Video::getOpen, 0)
                    .last("limit " + limit));
        }
        byte[] rankBytes = RedisConstant.HOT_RANK.getBytes();
        List<HotVideo> hotVideos = topK.get();
        Double minHot = hotVideos.get(0).getHot();
        redisCacheUtil.pipeline(connection -> {
            for (HotVideo hotVideo : hotVideos) {
                Double hot = hotVideo.getHot();
                hotVideo.setHot(null);
                try {
                    connection.zAdd(rankBytes, hot, Objects.requireNonNull(jackson2JsonRedisSerializer.serialize(objectMapper.writeValueAsBytes(hotVideo))));
                } catch (JsonProcessingException e) {
                    LOG.error(e.getMessage());
                }
            }
            return null;
        });
        redisCacheUtil.getRedisTemplate().opsForZSet().removeRangeByScore(RedisConstant.HOT_RANK, minHot, 0);
    }

    // 热门视频,没有热度排行榜实时且重要
    @Scheduled(cron = "0 0 */3 * * ?")
    @Override
    public void hotVideo() {
        final Double hotLimit = sysSettingMapper.selectList(new LambdaQueryWrapper<SysSetting>()).get(0).getHotLimit();
        long startId = 0L;
        int limit = 1000;
        LocalDateTime now = LocalDateTime.now();
        int dayOfMonth = now.getDayOfMonth();
        List<Video> videos = videoMapper.selectNDaysAgeVideo(startId, 999, limit);
        LOG.info("进行推送hotVideo..................");
        while (!ObjectUtils.isEmpty(videos)) {
            final ArrayList<Long> hotVideos = new ArrayList<>();
            for (Video video : videos) {
                Long shareCount = video.getShareCount();
                Double historyCount = video.getHistoryCount() * 0.8;
                Long startCount = video.getStartCount();
                Double favoritesCount = video.getFavoritesCount() * 1.5;
                long t = toEpochMilli(now) - toEpochMilli(video.getGmtCreated());

                final double hot = hot(shareCount + historyCount + startCount + favoritesCount,
                        TimeUnit.MILLISECONDS.toDays(t));
                // 大于X热度说明是热门视频
                if (hot >= hotLimit) {
                    hotVideos.add(video.getId());
                }
            }
            System.out.println("推送热门视频");
            if (!ObjectUtils.isEmpty(hotVideos)) {
                String key = RedisConstant.HOT_VIDEO + dayOfMonth;
                redisCacheUtil.getRedisTemplate().opsForSet().add(key, hotVideos.toArray());
                redisCacheUtil.getRedisTemplate().expire(key, 3, TimeUnit.DAYS);
                LOG.info("推送热门视频 key: {}" , key);
            }
            startId = videos.get(videos.size() - 1).getId();
            videos = videoMapper.selectNDaysAgeVideo(startId, 3, limit);
        }
    }


    private double hot(double weight, long days) {
        return weight * Math.exp(-a * days);
    }

    private double weightRandom() {
        int i = (int) ((Math.random() * 9 + 1) * 100000);
        return i / 1000000.0;
    }


}
