package com.lartimes.tiktok.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lartimes.tiktok.exception.BaseException;
import com.lartimes.tiktok.model.user.Captcha;
import com.lartimes.tiktok.model.user.User;
import com.lartimes.tiktok.model.vo.FindPWVO;
import com.lartimes.tiktok.model.vo.RegisterVO;
import com.lartimes.tiktok.service.user.CaptchaService;
import com.lartimes.tiktok.service.user.LoginService;
import com.lartimes.tiktok.service.user.UserService;
import com.lartimes.tiktok.util.RedisCacheUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/2 14:17
 */
@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger LOG = LogManager.getLogger(LoginServiceImpl.class);
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private UserService userService;

    @Override
    public void captcha(HttpServletResponse response, String uuId) throws IOException {
        if (ObjectUtils.isEmpty(uuId)) {
            LOG.warn("UUId is null");
            throw new IllegalArgumentException("uuId is null");
        }
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        final BufferedImage bufferedImage = captchaService.generateGraphCaptcha(uuId, response);
        final ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        IOUtils.closeQuietly(outputStream);
        LOG.info("获取验证码成功. UUID : {} ,image : {}", uuId, bufferedImage.toString());
    }

    @Override
    public boolean getCode(Captcha captcha) {
        return captchaService.sendEmailCode(captcha);
    }

    @Override
    public boolean checkEmailCode(String email, String captchaCode) {
        if (!redisCacheUtil.hashKey(email)) {
            LOG.error("邮箱验证码失效");
            throw new BaseException("邮箱验证码失效");
        }
        final String code = redisCacheUtil.getKey(email);
        if (!code.equals(captchaCode)) {
            LOG.error("验证码错误");
            throw new BaseException("验证码错误");
        }
        redisCacheUtil.deleteKey(email);
        LOG.info("邮箱验证码匹配成功，删除邮箱验证码 : {}", email);
        return true;
    }

    @Override
    public boolean register(RegisterVO registerVO) {
        if (!userService.registerUser(registerVO)) {
            return false;
        }
        final String uuid = registerVO.getUuid();
        return captchaService.removeById(uuid);
    }

    @Transactional
    @Override
    public boolean findPassword(FindPWVO findPWVO) {
        final String email = findPWVO.getEmail();
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(StringUtils.hasText(email), User::getEmail, email));
        if (ObjectUtils.isEmpty(user)) {
            LOG.info("该邮箱未被注册");
            throw new BaseException("该邮箱未被注册");
        }
        final String password =
                DigestUtils.md5Hex(findPWVO.getNewPassword());
        user.setPassword(password);
        LOG.info("更新密码: {}", user);
        final String key = redisCacheUtil.getKey(email);
        if (!StringUtils.hasText(key)) {
            LOG.info("邮箱验证码过期");
        } else {
            redisCacheUtil.deleteKey(email);
            LOG.info("删除邮箱验证码 : {}", email);
        }
        this.captchaService.removeById(findPWVO.getUuid());
        LOG.info("删除验证码，找回密码");
        return userService.updateById(user);
    }
}
