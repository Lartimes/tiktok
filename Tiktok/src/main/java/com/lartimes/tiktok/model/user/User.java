package com.lartimes.tiktok.model.user;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * <p>
 * 
 * </p>
 *
 * @author lartimes
 */
@Data
@TableName("user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String email;

    private String nickName;

    private String password;

    private String description;

    private Boolean sex;

    private String avatar;

    private Long defaultFavoritesId;

    private Integer isDeleted;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtUpdated;

    @TableField(exist = false)
    private Boolean each;

    @TableField(exist = false)
    private Set<String> roleName;


}
