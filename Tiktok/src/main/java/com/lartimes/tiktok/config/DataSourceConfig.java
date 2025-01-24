package com.lartimes.tiktok.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/24 21:20
 */
@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource longConnectionDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/tiktok");
        dataSource.setUsername("root");
        dataSource.setPassword("307314");
        dataSource.setMaximumPoolSize(5); // 根据需要调整池大小
        dataSource.setMinimumIdle(2);    // 最小空闲连接数
        dataSource.setIdleTimeout(300000); // 5 分钟（300,000 毫秒）
        dataSource.setMaxLifetime(300000); // 5 分钟（300,000 毫秒）
        return dataSource;
    }
}
