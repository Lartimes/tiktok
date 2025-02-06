package com.lartimes.tiktok.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/25 13:29
 */
@Configuration
public class MyBatisConfig {
    @Bean
    public MySqlInjector sqlInjector() {
        return new MySqlInjector();
    }


    @Bean
    public GlobalConfig globalConfig(@Qualifier("sqlInjector") ISqlInjector sqlInjector) {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setSqlInjector(sqlInjector);
        return globalConfig;
    }

    @Bean("batchSqlSessionFactory")
    public SqlSessionFactory batchSqlSessionFactory(@Qualifier("globalConfig") GlobalConfig globalConfig,
                                                    @Qualifier("longConnectionDataSource") DataSource longConnectionDataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(longConnectionDataSource);
        sqlSessionFactory.setGlobalConfig(globalConfig);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactory.setMapperLocations(resolver.getResources("classpath:com/lartimes/tiktok/mapper/*.xml"));
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setUseGeneratedKeys(true);
        configuration.setAutoMappingBehavior(AutoMappingBehavior.FULL);
        sqlSessionFactory.setConfiguration(configuration);
        return sqlSessionFactory.getObject();
    }

}
