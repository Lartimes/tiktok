package com.lartimes.tiktok.controller;

import com.lartimes.tiktok.model.user.Captcha;
import com.lartimes.tiktok.model.user.User;
import com.lartimes.tiktok.model.vo.FindPWVO;
import com.lartimes.tiktok.model.vo.RegisterVO;
import com.lartimes.tiktok.service.LoginService;
import com.lartimes.tiktok.service.UserService;
import com.lartimes.tiktok.util.JWTUtils;
import com.lartimes.tiktok.util.R;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/1 23:08
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    private static final Logger LOG = LogManager.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;
    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtils jwtUtils;

    @PostMapping
    public R login(@RequestBody @Validated User user) {
        String md5Password = DigestUtils.md5Hex(user.getPassword());
        user.setPassword(md5Password);
        final User login = userService.login(user);
        // 登录成功，生成token
        String token = jwtUtils.getToken(login.getId(), login.getNickName());
        final HashMap<Object, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("name", login.getNickName());
        map.put("user", login);
        LOG.info("登录成功. {}", login);
        return R.ok().data(map);
    }

    @GetMapping("/captcha.jpg/{uuId}")
    public void captcha(HttpServletResponse response, @PathVariable String uuId) throws IOException {
        loginService.captcha(response, uuId);
        LOG.info("生成验证码成功 {}", uuId);
    }

    @PostMapping("/getCode")
    public R getCode(@RequestBody
                     @Validated Captcha captcha) {
        if (!loginService.getCode(captcha)) {
            return R.error().message("验证码错误");
        }
        LOG.info("发送验证码成功");
        return R.ok().message("发送成功,请耐心等待");
    }

    @PostMapping("/check")
    public R check(@RequestParam("email") String email,
                   @RequestParam("code") String captchaCode) {
        if (email == null || captchaCode == null) {
            throw new IllegalArgumentException("参数不正确");
        }
        if (!loginService.checkEmailCode(email, captchaCode)) {
            return R.error().message("邮箱验证码错误");
        }
        LOG.info("校验成功，可以进行注册/更改密码了");
        return R.ok().message("正确");
    }


    @PostMapping("/register")
    public R register(@RequestBody @Validated RegisterVO registerVO) {
        if (!loginService.register(registerVO)) {
            return R.error().message("注册失败,验证码错误");
        }
        LOG.info("注册成功 : {}", registerVO);
        return R.ok().message("注册成功");
    }

    @PostMapping("/findPassword")
    public R findPassword(@RequestBody @Validated FindPWVO findPWVO) {
        if (!loginService.findPassword(findPWVO)) {
            return R.error().message("请重试.");
        }
        LOG.info("找回密码成功 : {}", findPWVO);
        return R.ok().message("找回密码成功");
    }


}
