package com.lartimes.tiktok.model.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/2 19:25
 */
@Data
public class FindPWVO {

    @Email(message = "邮箱格式不正确")
    String email;

    @NotNull(message =  "captcha不能为空")
    String captchaCode;

    @NotNull(message =  "uuid不能为空")
    String uuid;

    @NotNull(message = "code不能为空")
    Integer code;

    @NotBlank(message = "新密码不能为空")
    String newPassword;
}
