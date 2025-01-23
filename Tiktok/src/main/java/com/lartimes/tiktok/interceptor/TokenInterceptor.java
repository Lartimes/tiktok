package com.lartimes.tiktok.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lartimes.tiktok.holder.UserHolder;
import com.lartimes.tiktok.model.user.User;
import com.lartimes.tiktok.service.UserService;
import com.lartimes.tiktok.util.JWTUtils;
import com.lartimes.tiktok.util.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/2 21:29
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LogManager.getLogger(TokenInterceptor.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private UserService userService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOG.info("进入preHandle method : {}", request.getRequestURI());
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        if (!jwtUtils.checkToken(request)) {
            response(R.error().message("请登录后再操作"), response);
            return false;
        }
        final Long userId = jwtUtils.getUserId(request);
        final User user = userService.getById(userId);
        if (ObjectUtils.isEmpty(user)) {
            response(R.error().message("用户不存在"), response);
            return false;
        }
        LOG.info("放入UserHolder :{}", userId.toString());
        UserHolder.set(userId);
        return true;
    }

    private boolean response(R r, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(objectMapper.writeValueAsString(r));
        response.getWriter().flush();
        return false;
    }
}
