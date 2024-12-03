package com.lartimes.tiktok.service;

import com.lartimes.tiktok.model.po.Captcha;
import com.lartimes.tiktok.model.vo.FindPWVO;
import com.lartimes.tiktok.model.vo.RegisterVO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/2 14:16
 */
public interface LoginService {

    void captcha(HttpServletResponse response, String uuId) throws IOException;

    boolean getCode(Captcha captcha);

    boolean checkEmailCode(String email, String captchaCode);

    boolean register(RegisterVO registerVO);

    boolean findPassword(FindPWVO findPWVO);

}
