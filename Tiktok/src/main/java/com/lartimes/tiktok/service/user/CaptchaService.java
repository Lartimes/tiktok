package com.lartimes.tiktok.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lartimes.tiktok.model.user.Captcha;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;

/**
 * <p>
 * 系统验证码 服务类
 * </p>
 *
 * @author lartimes
 * @since 2024-12-02
 */
public interface CaptchaService extends IService<Captcha> {

    /**
     * 生成图形二维码 ttl 5min
     * @param uuId
     * @param response
     */
    BufferedImage generateGraphCaptcha(String uuId, HttpServletResponse response);

    /**
     * 判断参数，并且发送邮箱验证码
     * @param captcha
     */
    Boolean sendEmailCode(Captcha captcha);

}
