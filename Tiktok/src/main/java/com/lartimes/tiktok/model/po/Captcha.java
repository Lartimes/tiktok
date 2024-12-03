package com.lartimes.tiktok.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 系统验证码
 * </p>
 *
 * @author lartimes
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("captcha")
public class Captcha implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    /**
     * uuid
     */
    @NotBlank(message = "uuid为空")
    @TableId
    private String uuid;

    /**
     * 验证码
     */
    @NotBlank(message = "code为空")
    private String code;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;


    @TableField(exist = false)
    @Email
    private String email;

}
