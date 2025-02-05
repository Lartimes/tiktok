package com.lartimes.tiktok.config;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "qiniu.kodo")
public class QiNiuConfig implements InitializingBean {

    public static String CNAME;
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
        String accessKey = this.getAccessKey();
        String secretKey = this.getSecretKey();
        return Auth.create(accessKey, secretKey);
    }

    public String getToken(String type) {
        return getAuth().uploadToken(bucket, null, 300,
                new StringMap().put("mimeLimit", mediaType + ";" + imageType), true);
    }
    public String getToken(String url, String method, String body, String contentType) {
        final Auth auth = getAuth();
        return "Qiniu " + auth.signQiniuAuthorization(url, method, body == null ?
                        null : body.getBytes(), contentType);
    }

    public String videoGetToken() {
        return getAuth().uploadToken(bucket, null, 300,
                new StringMap().put("mimeLimit", mediaType)
                        .putNotEmpty("persistentOps", fops));
    }

    public String imageGetToken() {
        return getAuth().uploadToken(bucket, null, 300,
                new StringMap().put("mimeLimit", imageType));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CNAME = this.cname;
    }
}
