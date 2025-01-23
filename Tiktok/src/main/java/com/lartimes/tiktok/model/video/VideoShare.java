package com.lartimes.tiktok.model.video;


import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("video_share")
public class VideoShare implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long videoId;

    private String ip;

    private Long userId;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtUpdated;


}
