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
@TableName("type")
public class Type implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private Boolean open;

    private String icon;

    private Integer sort;

    private String labelNames;

    @TableField("is_deleted")
    private Boolean deleted;
    @TableField(exist = false)
    private Boolean used;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtUpdated;


}
