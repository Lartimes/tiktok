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
@TableName("file")
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String fileKey;

    private String format;

    private String type;

    private String duration;

    private Long size;

    private Long userId;

    private Integer isDeleted;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtUpdated;


}
