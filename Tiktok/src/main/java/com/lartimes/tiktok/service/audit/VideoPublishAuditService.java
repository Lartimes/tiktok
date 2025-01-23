package com.lartimes.tiktok.service.audit;

import com.lartimes.tiktok.config.QiNiuConfig;
import com.lartimes.tiktok.constant.AuditStatus;
import com.lartimes.tiktok.mapper.VideoMapper;
import com.lartimes.tiktok.model.audit.AuditResponse;
import com.lartimes.tiktok.model.task.VideoTask;
import com.lartimes.tiktok.model.video.Video;
import com.lartimes.tiktok.service.FileService;
import com.lartimes.tiktok.service.FollowService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/23 19:18
 */
@Service
public class VideoPublishAuditService implements AuditService<VideoTask, AuditResponse>, InitializingBean, BeanPostProcessor {
    private static final Logger LOG = LogManager.getLogger(VideoPublishAuditService.class);
    private final VideoMapper videoMapper;
    private final VideoAuditService videoAuditService;
    private final ImageAuditService imageAuditService;
    private final TextAuditService textAuditService;
    private final FileService fileService;
    private final FollowService followService;
    protected ThreadPoolExecutor executor;
    private int maximumPoolSize = 8;

    {
        this.maximumPoolSize = Runtime.getRuntime().availableProcessors() * 2;
    }

    public VideoPublishAuditService(VideoMapper videoMapper, VideoAuditService videoAuditService, ImageAuditService imageAuditService, TextAuditService textAuditService, FileService fileService, FollowService followService) {
        this.videoMapper = videoMapper;
        this.videoAuditService = videoAuditService;
        this.imageAuditService = imageAuditService;
        this.textAuditService = textAuditService;
        this.fileService = fileService;
        this.followService = followService;
    }


    /**
     * @param videoTask
     * @param auditQueueState 申请快/慢审核
     * @return
     */
    public VideoTask audit(VideoTask videoTask, Boolean auditQueueState) {
        LOG.info("视频审核阶段 , 进入{}审核", getAuditQueueState() ? "快" : "慢");
        if (getAuditQueueState()) {
            new Thread(() -> {
                audit(videoTask);
            }).start();
        } else {
            audit(videoTask);
        }
        return null;
    }

    @Override
    public AuditResponse audit(VideoTask videoTask) {
//           修改的话，新老状态不一致才审核， 否则返回之前的
        executor.submit(() -> {
            final Video video = videoTask.getVideo();
            final Video video1 = new Video();
            BeanUtils.copyProperties(video, video1);
            // 只有视频在新增或者公开时候才需要调用审核视频/封面
            // 新增 ： 必须审核
            // 修改: 新老状态不一致
            // 需要审核视频/封面
            boolean needAuditVideo = false;
            if (videoTask.getIsAdd() && videoTask.getOldState() == videoTask.getNewState()) {
                needAuditVideo = true;
            } else if (!videoTask.getIsAdd() && videoTask.getOldState() != videoTask.getNewState()) {
                // 修改的情况下新老状态不一致,说明需要更新
                if (!videoTask.getNewState()) {
                    needAuditVideo = true;
                }
            }
            AuditResponse videoAuditResponse = new AuditResponse(AuditStatus.SUCCESS, "正常");
            AuditResponse coverAuditResponse = new AuditResponse(AuditStatus.SUCCESS, "正常");
            AuditResponse titleAuditResponse = new AuditResponse(AuditStatus.SUCCESS, "正常");
            AuditResponse descAuditResponse = new AuditResponse(AuditStatus.SUCCESS, "正常");

            if (needAuditVideo) {
                videoAuditResponse = videoAuditService.audit(QiNiuConfig.CNAME + "/" + fileService.getById(video.getUrl()).getFileKey());
                coverAuditResponse = imageAuditService.audit(QiNiuConfig.CNAME + "/" + fileService.getById(video.getCover()).getFileKey());


//                interestPushService.pushSystemTypeStockIn(video1);
//                interestPushService.pushSystemStockIn(video1);

                // 推入发件箱
//                feedService.pusOutBoxFeed(video.getUserId(), video.getId(), video1.getGmtCreated().getTime());
            } else if (videoTask.getNewState()) {
//                interestPushService.deleteSystemStockIn(video1);
//                interestPushService.deleteSystemTypeStockIn(video1);
                // 删除发件箱以及收件箱
                final Collection<Long> fans = followService.getFansCollection(video.getUserId(), null);
//                feedService.deleteOutBoxFeed(video.getUserId(), fans, video.getId());
            }

            // 新老视频标题简介一致
            final Video oldVideo = videoTask.getOldVideo();
            if (!video.getTitle().equals(oldVideo.getTitle())) {
                titleAuditResponse = textAuditService.audit(video.getTitle());
            }
            if (!video.getDescription().equals(oldVideo.getDescription()) && !ObjectUtils.isEmpty(video.getDescription())) {
                descAuditResponse = textAuditService.audit(video.getDescription());
            }

            final Integer videoAuditStatus = videoAuditResponse.getAuditStatus();
            final Integer coverAuditStatus = coverAuditResponse.getAuditStatus();
            final Integer titleAuditStatus = titleAuditResponse.getAuditStatus();
            final Integer descAuditStatus = descAuditResponse.getAuditStatus();
            boolean f1 = Objects.equals(videoAuditStatus, AuditStatus.SUCCESS);
            boolean f2 = Objects.equals(coverAuditStatus, AuditStatus.SUCCESS);
            boolean f3 = Objects.equals(titleAuditStatus, AuditStatus.SUCCESS);
            boolean f4 = Objects.equals(descAuditStatus, AuditStatus.SUCCESS);

            if (f1 && f2 && f3 && f4) {
                video1.setMsg("通过");
                video1.setAuditStatus(AuditStatus.SUCCESS);
                // 填充视频时长
            } else {
                video1.setAuditStatus(AuditStatus.PASS);
                // 避免干扰
                video1.setMsg("");
                if (!f1) {
                    video1.setMsg("视频有违规行为: " + videoAuditResponse.getMsg());
                }
                if (!f2) {
                    video1.setMsg(video1.getMsg() + "\n封面有违规行为: " + coverAuditResponse.getMsg());
                }
                if (!f3) {
                    video1.setMsg(video1.getMsg() + "\n标题有违规行为: " + titleAuditResponse.getMsg());
                }
                if (!f4) {
                    video1.setMsg(video1.getMsg() + "\n简介有违规行为: " + descAuditResponse.getMsg());
                }
            }
            videoMapper.updateById(video1);
            LOG.info("审核响应video :{}", videoAuditResponse);
            LOG.info("审核响应image :{}", coverAuditResponse);
            LOG.info("审核响应title :{}", titleAuditResponse);
        });

        return null;

    }

    public boolean getAuditQueueState() {
        return executor.getTaskCount() < maximumPoolSize;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        executor = new ThreadPoolExecutor(5, maximumPoolSize, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000));
    }
}
