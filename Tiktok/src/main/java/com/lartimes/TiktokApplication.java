package com.lartimes;

import com.lartimes.tiktok.schedule.VideoScheduledService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author wüsch
 * @version 1.0
 * @description: 主程序
 * @since 2024/12/1 22:16
 */
@EnableCaching
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication
@MapperScan(basePackages = "com.lartimes.tiktok.mapper")
public class TiktokApplication implements ApplicationRunner {
//TODO : 后续搞一个上传资源过滤的 使用jdk21 的并发线程 + 轮询 短时优先算法？
//           搞一个过滤系统 ， 评判用户模型 引入大数据框 互相操作


    @Autowired
    private VideoScheduledService videoScheduledService;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TiktokApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("进行测试");
        videoScheduledService.updateVideoStar();
    }

}
