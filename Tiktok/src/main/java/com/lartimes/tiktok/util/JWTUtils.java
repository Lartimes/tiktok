package com.lartimes.tiktok.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/2 18:10
 */
@ConfigurationProperties("jwt.config")
@Component
@Setter
@Getter
public class JWTUtils {
    private static final Logger LOG = LogManager.getLogger(JWTUtils.class);
    public long expire;
    public String secret;

    /**
     * 判断token是否存在与有效
     *
     * @param request
     * @return
     */
    public boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("token");
            if (ObjectUtils.isEmpty(jwtToken)) return false;
            JWT.require(Algorithm.HMAC256(secret)).build().verify(jwtToken);
            return Boolean.TRUE;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserId(HttpServletRequest request) {
        String jwtToken = request.getHeader("token");
        if (ObjectUtils.isEmpty(jwtToken)) return null;
        if (!this.checkToken(jwtToken)) {
            LOG.info("token无效");
            return null;
        }
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(secret)).build().verify(jwtToken);
        return verify.getClaim("id").asLong();
    }

    /**
     * 判断token是否存在与有效
     *
     * @param jwtToken
     * @return
     */
    public boolean checkToken(String jwtToken) {
        if (!StringUtils.hasText(jwtToken)) return false;
        try {
            JWT.require(Algorithm.HMAC256(secret)).build().verify(jwtToken);
            LOG.info("验证正确");
            return Boolean.TRUE;
        } catch (Exception e) {
            LOG.error("验证失败");
            return Boolean.FALSE;
        }
    }

    public String getToken(Long id, String username) {
        Date now = new Date();
        Date then = new Date(now.getTime() + expire);
        HashMap<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        return JWT.create()
                .withHeader(map)
                .withIssuer("wüsch")
                .withIssuedAt(now)
                .withExpiresAt(then)
                .withClaim("id", id)
                .withClaim("username", username)
                .sign(Algorithm.HMAC256(secret));
    }
}
