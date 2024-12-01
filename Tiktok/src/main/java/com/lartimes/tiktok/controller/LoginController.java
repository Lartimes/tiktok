package com.lartimes.tiktok.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    //    login/captcha.jpg/dca04bbd-54bd-4c69-a806-2e24f6593815
    ///luckyjourney/login
    @GetMapping("/captcha.jpg/{uuId}")
    public void captcha(HttpServletResponse response, @PathVariable String uuId) throws IOException {
        System.out.println(uuId);
    }

}
