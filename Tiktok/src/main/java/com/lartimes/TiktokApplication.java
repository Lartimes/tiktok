package com.lartimes;

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
 * @description: 主程序
 * @since 2024/12/1 22:16
 */
@EnableCaching
@EnableTransactionManagement
@SpringBootApplication
@MapperScan(basePackages = "com.lartimes.tiktok.mapper")
public class TiktokApplication implements ApplicationRunner {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TiktokApplication.class, args);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
