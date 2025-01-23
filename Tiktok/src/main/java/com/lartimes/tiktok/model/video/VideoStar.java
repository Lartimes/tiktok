package com.lartimes.tiktok.model.video;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author lartimes
 */
@Data
@TableName("video_star")
public class VideoStar implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long videoId;

    private Long userId;

    @TableField("is_deleted")
    private Boolean deleted;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtUpdated;


}
