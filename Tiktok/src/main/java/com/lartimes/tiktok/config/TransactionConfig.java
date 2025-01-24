package com.lartimes.tiktok.config;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/24 21:44
 */

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class TransactionConfig {


    @Bean
    public PlatformTransactionManager longConnectionTransactionManager(
            @Qualifier("longConnectionDataSource")
            DataSource longConnectionDataSource) {
        return new DataSourceTransactionManager(longConnectionDataSource);
    }
}
