package com.lartimes.tiktok.service.user.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.code.kaptcha.Producer;
import com.lartimes.tiktok.exception.BaseException;
import com.lartimes.tiktok.mapper.CaptchaMapper;
import com.lartimes.tiktok.model.user.Captcha;
import com.lartimes.tiktok.service.user.CaptchaService;
import com.lartimes.tiktok.service.user.UserService;
import com.lartimes.tiktok.util.EmailSenderUtil;
import com.lartimes.tiktok.util.RedisCacheUtil;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

/**
 * <p>
 * 系统验证码 服务实现类
 * </p>
 *
 * @author lartimes
 */
@Service
public class CaptchaServiceImpl extends ServiceImpl<CaptchaMapper, Captcha> implements CaptchaService {
    private static final Logger LOG = LogManager.getLogger(CaptchaServiceImpl.class);

    @Resource
    private Producer captchaProducer;
    @Autowired
    private EmailSenderUtil emailSenderUtil;
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private UserService userService;

    private static String generateVerificationCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    @GetMapping("/captchaImage")
    @Transactional
    public BufferedImage generateGraphCaptcha(String uuId, HttpServletResponse response) {
        String code = captchaProducer.createText();
        Captcha captcha = Captcha.builder().uuid(uuId)
                .expireTime(LocalDateTime.now().plusMinutes(5))
                .code(code).build();
        this.save(captcha);
        LOG.info("将captcha 保存到DB中 , captcha: {}", captcha.toString());
        return captchaProducer.createImage(code);
    }

    @Override
    public Boolean sendEmailCode(Captcha captcha) {
        final String email = captcha.getEmail();
        if (redisCacheUtil.hashKey(email)) {
            throw new BaseException("请一分钟后重新发送验证码");
        }
        final String uuid = captcha.getUuid();
        Captcha byId = this.getBaseMapper().selectById(uuid);
        if (byId == null) {
            LOG.error("不存在该验证码");
            throw new BaseException("请重新刷新验证码");
        }
        if (byId.getExpireTime().isBefore(LocalDateTime.now())) {
            LOG.error("验证码失效:{}", captcha);
            throw new BaseException("验证码失效");
        }
        if (!Objects.equals(byId.getCode(), captcha.getCode())) {
            LOG.error("验证码不正确:{}", captcha);
            throw new BaseException("验证码不正确");
        }

        final String verificationCode = generateVerificationCode(6);
        LOG.info("发送邮箱验证码 TO : {} , CODE : {}", email, verificationCode);
        try {
            emailSenderUtil.sentTo(email, verificationCode);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new BaseException("邮箱发送失败");
        }
        redisCacheUtil.set(email, verificationCode, 60);
        return Boolean.TRUE;
    }
}
