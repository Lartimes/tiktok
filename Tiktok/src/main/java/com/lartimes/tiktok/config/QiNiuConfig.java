package com.lartimes.tiktok.config;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.StringJoiner;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/3 10:12
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "qiniu.kodo")
public class QiNiuConfig {

    public String fops;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String mediaType;
    private String imageType;
    private String cname;
    private String videoUrl;
    private String imageUrl;


    public Auth getAuth() {
        return Auth.create(accessKey, secretKey);
    }

    public String getToken(String type) {
        return getAuth().uploadToken(bucket, null, 300,
                new StringMap().put("mimeLimit", new StringJoiner(";").add(mediaType).add(imageType).toString()));
    }

    public String videoGetToken() {
        return getAuth().uploadToken(bucket, null, 300,
                new StringMap().put("mimeLimit", new StringJoiner(";").add(mediaType)
                        .toString()).putNotEmpty("persistentOps", fops));
    }

    public String imageGetToken() {
        return getAuth().uploadToken(bucket, null, 300,
                new StringMap().put("mimeLimit", new StringJoiner(";").add(imageType)
                        .toString()));
    }
}
