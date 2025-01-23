package com.lartimes.tiktok.model.video;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lartimes.tiktok.config.QiNiuConfig;
import com.lartimes.tiktok.model.vo.UserVO;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author lartimes
 */
@Data
@TableName("video")
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String yv;

    private String title;

    private String description;

    private Long url;

    private Long userId;

    /**
     * 类别id
     */
    private Long typeId;

    /**
     * 公开/私密，0：公开，1：私密，默认为0
     */
    private Boolean open;

    private Long cover;

    private Integer auditStatus;

    private String msg;
    // 视频分类
    @TableField(exist = false)
    private String videoType;
    // 关联的用户
    @TableField(exist = false)
    private UserVO user;
    // 关联分类名称
    @TableField(exist = false)
    private String typeName;
    // 是否点赞
    @TableField(exist = false)
    private Boolean start;

    // 是否收藏
    @TableField(exist = false)
    private Boolean favorites;

    // 是否关注
    @TableField(exist = false)
    private Boolean follow;

    // 用户昵称
    @TableField(exist = false)
    private String userName;

    // 审核状态名称
    @TableField(exist = false)
    private String auditStateName;

    // 是否公开
    @TableField(exist = false)
    private String openName;
    /**
     * 审核队列状态
     */
    private Integer auditQueueStatus;

    private Long startCount;

    private Long shareCount;

    private Long historyCount;

    private Long favoritesCount;

    private String labelNames;


    private String duration;

    /**
     * 逻辑删除，0：未删除，1：删除，默认为0
     */
    @TableField("is_deleted")
    private Boolean deleted;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtUpdated;


    public List<String> buildLabel() {
        if (ObjectUtils.isEmpty(this.labelNames)) return Collections.emptyList();
        return Arrays.asList(this.labelNames.split(","));
    }


    // 和get方法分开，避免发生歧义
    public String getVideoUrl() {
        return QiNiuConfig.CNAME + "/" + this.url;
    }

    public String getCoverUrl() {
        return QiNiuConfig.CNAME + "/" + this.cover;
    }


}
