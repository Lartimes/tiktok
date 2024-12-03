package com.lartimes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/1 22:16
 */
@EnableCaching
@EnableTransactionManagement
@SpringBootApplication
@MapperScan(basePackages = "com.lartimes.tiktok.mapper")
public class TiktokApplication implements ApplicationRunner {
    private static final Logger LOG = LogManager.getLogger(TiktokApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TiktokApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOG.info("启动成功 ,args : {}", args);
    }
}
