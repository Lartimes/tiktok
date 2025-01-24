package com.lartimes.tiktok.schedule.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lartimes.tiktok.constant.RedisConstant;
import com.lartimes.tiktok.exception.BaseException;
import com.lartimes.tiktok.mapper.VideoMapper;
import com.lartimes.tiktok.mapper.VideoStarMapper;
import com.lartimes.tiktok.model.video.Video;
import com.lartimes.tiktok.model.video.VideoStar;
import com.lartimes.tiktok.schedule.VideoScheduledService;
import com.lartimes.tiktok.util.RedisCacheUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Set;
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
    private final SqlSessionFactory sqlSessionFactory;
    private final DataSource longConnectionDataSource;
    private final RedisCacheUtil redisCacheUtil;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private VideoStarMapper videoStarMapper;

    public VideoScheduledServiceImpl(@Qualifier("longConnectionDataSource") DataSource longConnectionDataSource, SqlSessionFactory sqlSessionFactory, RedisCacheUtil redisCacheUtil) {
        this.longConnectionDataSource = longConnectionDataSource;
        this.sqlSessionFactory = sqlSessionFactory;
        this.redisCacheUtil = redisCacheUtil;
    }

    @Scheduled(cron = "0 0/30 * * * ?") // 每 30 分钟的第 0 秒执行一次
    @Override
    public void updateVideoStar() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(longConnectionDataSource.getConnection())) {
//                list foreach ---> 执行？
            updateVideoStar(sqlSession);
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
        try (Connection connection = sqlSession.getConnection()) {
            connection.setAutoCommit(false);
            VideoStarMapper mapper = sqlSession.getMapper(VideoStarMapper.class);
//            两个集合进行筛选出独特的 一个remove ， 一个update
            for (Long videoId : videoIds) {
                String tmp = "diff:" + videoId;
                Set<Long> removedIds = redisCacheUtil.differenceIntersectionAlternative("DB:" + RedisConstant.VIDEO_LIKE_IDS + videoId,
                        RedisConstant.VIDEO_LIKE_IDS + videoId, tmp).stream().map((id -> Long.parseLong(id.toString()))).collect(Collectors.toSet());
                Set<VideoStar> addedStars = redisCacheUtil.differenceIntersectionAlternative(RedisConstant.VIDEO_LIKE_IDS + videoId,
                                "DB:" + RedisConstant.VIDEO_LIKE_IDS + videoId, tmp).stream()
                        .map(((id -> {
                            VideoStar videoStar = new VideoStar();
                            videoStar.setId(null);
                            videoStar.setGmtUpdated(LocalDateTime.now());
                            videoStar.setUserId(Long.parseLong(id.toString()));
                            videoStar.setVideoId(videoId);
                            return videoStar;
                        }))).collect(Collectors.toSet());
                //                db - db ∩ redis
//                redis - db ∩ redis
                mapper.deleteBatchIds(removedIds);
                int size = addedStars.size();
                for (int i = 0; i < size; i += BATCH_SIZE) {
                    int end = Math.min(i + BATCH_SIZE, size);
//                    TODO 批量插入
                    boolean result = this.saveBatch(userList.subList(i, end));
                }


            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new BaseException(e.getMessage());
        }
    }
}
