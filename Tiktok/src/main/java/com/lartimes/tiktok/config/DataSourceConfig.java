package com.lartimes.tiktok.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/24 21:20
 */
@Configuration
public class DataSourceConfig {
    @Value("${spring.datasource.url}")
    private String url;


    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;


    @Primary
    @Bean
    public DataSource longConnectionDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        String replace = url.replace("p6spy:", "");
        dataSource.setJdbcUrl(replace);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaximumPoolSize(5); // 根据需要调整池大小
        dataSource.setMinimumIdle(2);    // 最小空闲连接数
        dataSource.setIdleTimeout(300000); // 5 分钟（300,000 毫秒）
        dataSource.setMaxLifetime(300000); // 5 分钟（300,000 毫秒）
        return dataSource;
    }
}
