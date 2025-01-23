package com.lartimes.tiktok.service.audit;

import com.lartimes.tiktok.constant.AuditStatus;
import com.lartimes.tiktok.model.SysSetting;
import com.lartimes.tiktok.model.audit.AuditResponse;
import com.lartimes.tiktok.model.audit.BodyJson;
import com.lartimes.tiktok.model.audit.ScoreJson;
import com.lartimes.tiktok.model.audit.SettingScoreJson;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.StringMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/23 19:14
 */
@Service
public class VideoAuditService extends AbstractAuditService<String, AuditResponse> {
    private static final Logger LOG = LogManager.getLogger(VideoAuditService.class);

    static String videoUrl = "http://ai.qiniuapi.com/v3/video/censor";
    static String videoBody = "{\n" +
            "    \"data\": {\n" +
            "        \"uri\": \"${url}\",\n" +
            "        \"id\": \"video_censor_test\"\n" +
            "    },\n" +
            "    \"params\": {\n" +
            "        \"scenes\": [\n" +
            "            \"pulp\",\n" +
            "            \"terror\",\n" +
            "            \"politician\"\n" +
            "        ],\n" +
            "        \"cut_param\": {\n" +
            "            \"interval_msecs\": 5000\n" +
            "        }\n" +
            "    }\n" +
            "}";


    @Override
    public AuditResponse audit(String url) {

        AuditResponse auditResponse = new AuditResponse();
        auditResponse.setAuditStatus(AuditStatus.SUCCESS);

        if (!isNeedAudit()) {
            return auditResponse;
        }
        url = appendUUID(url);

        String body = videoBody.replace("${url}", url);
        String method = "POST";
        // 获取token
        final String token = qiNiuConfig.getToken(videoUrl, method, body, contentType);
        StringMap header = new StringMap();
        header.put("Host", "ai.qiniuapi.com");
        header.put("Authorization", token);
        header.put("Content-Type", contentType);
        Configuration cfg = new Configuration(Region.region2());
        final Client client = new Client(cfg);
        try {
            Response response = client.post(videoUrl, body.getBytes(), header, contentType);
            final Map map = objectMapper.readValue(response.getInfo().split(" \n")[2], Map.class);
            final Object job = map.get("job");
            url = "http://ai.qiniuapi.com/v3/jobs/video/" + job.toString();
            method = "GET";
            header = new StringMap();
            header.put("Host", "ai.qiniuapi.com");
            header.put("Authorization", qiNiuConfig.getToken(url, method, null, null));
            while (true) {
                Response response1 = client.get(url, header);
                final BodyJson bodyJson = objectMapper.readValue(response1.getInfo().split(" \n")[2], BodyJson.class);
                if (bodyJson.getStatus().equals("FINISHED")) {
                    // 1.从系统配置表获取 pulp politician terror比例
                    final SysSetting setting = settingService.getById(1);
                    final SettingScoreJson settingScoreRule = objectMapper.readValue(setting.getAuditPolicy(), SettingScoreJson.class);
                    final List<ScoreJson> auditRule = Arrays.asList(settingScoreRule.getManualScore(), settingScoreRule.getPassScore(), settingScoreRule.getSuccessScore());
                    auditResponse = audit(auditRule, bodyJson);
                    return auditResponse;
                }
                Thread.sleep(2000L);
            }
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
        return auditResponse;
    }
}

