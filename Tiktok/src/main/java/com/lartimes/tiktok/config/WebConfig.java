package com.lartimes.tiktok.config;

import com.lartimes.tiktok.interceptor.TokenInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wüsch
 * @version 1.0
 * @description: web config配置类
 * @since 2024/12/2 21:47
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final Logger LOG = LogManager.getLogger(WebConfig.class);

    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LOG.info("加载拦截器");
        LOG.info("{}", this.tokenInterceptor);
        registry.addInterceptor(this.tokenInterceptor)
                .addPathPatterns("/admin/**", "/authorize/**")
                .addPathPatterns("/customer/**")
                .addPathPatterns("/tiktok/**")
                .excludePathPatterns("/tiktok/login/**",
                        "/tiktok/index/**",
                        "/tiktok/cdn/**");
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {

        String[] url = {"http://127.0.0.1:314", "http://127.0.0.1:5378", "http://localhost:5378"};

        registry.addMapping("/**")
                .allowedOrigins(url)
                .allowCredentials(true)
                .allowedMethods("*")   // 允许跨域的方法，可以单独配置
                .allowedHeaders("*");  // 允许跨域的请求头，可以单独配置;
    }


}
