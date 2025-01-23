package com.lartimes.tiktok.service.audit;

import com.lartimes.tiktok.config.QiNiuConfig;
import com.lartimes.tiktok.constant.AuditStatus;
import com.lartimes.tiktok.model.SysSetting;
import com.lartimes.tiktok.model.audit.*;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.StringMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/23 15:52
 */
@Service
public class ImageAuditService extends AbstractAuditService<String, AuditResponse> {
    private static final Logger LOG = LogManager.getLogger(ImageAuditService.class);
    static String imgUrl = "http://ai.qiniuapi.com/v3/image/censor";
    static String method = "POST";
    static String imageBody = "{\n" +
            "    \"data\": {\n" +
            "        \"uri\": \"${url}\"\n" +
            "    },\n" +
            "    \"params\": {\n" +
            "        \"scenes\": [\n" +
            "            \"pulp\",\n" +
            "            \"terror\",\n" +
            "            \"politician\"\n" +
            "        ]\n" +
            "    }\n" +
            "}";


    @Override
    public AuditResponse audit(String url) {
        //TODO: url 如果是第一帧，会超时，必须配置新的token / header 等
//         所以上传视频的时候,回调的时候存入视频第一帧, 直接拿取
        AuditResponse auditResponse = new AuditResponse();
        auditResponse.setAuditStatus(AuditStatus.SUCCESS);
        if (!isNeedAudit()) {
            return auditResponse;
        }
        try {
            if (!url.contains(QiNiuConfig.CNAME)) {
                String encodedFileName = URLEncoder.encode(url, StandardCharsets.UTF_8).replace("+", "%20");
                url = String.format("%s/%s", QiNiuConfig.CNAME, encodedFileName);
            }
            url = appendUUID(url);
            String body = imageBody.replace("${url}", url);
            // 获取token
            final String token = qiNiuConfig.getToken(imgUrl, method, body, contentType);
            StringMap header = new StringMap();
            header.put("Host", "ai.qiniuapi.com");
            header.put("Authorization", token);
            header.put("Content-Type", contentType);
            Configuration cfg = new Configuration(Region.region2());
            final Client client = new Client(cfg);
            Response response = client.post(imgUrl, body.getBytes(), header, contentType);

            final Map map = objectMapper.readValue(response.getInfo().split(" \n")[2], Map.class);
            final ResultChildJson result = objectMapper.convertValue(map.get("result"), ResultChildJson.class);
            final BodyJson bodyJson = new BodyJson();
            final ResultJson resultJson = new ResultJson();
            resultJson.setResult(result);
            bodyJson.setResult(resultJson);

            final SysSetting setting = settingService.getById(1);
            final SettingScoreJson settingScoreRule = objectMapper.readValue(
                    setting.getAuditPolicy(), SettingScoreJson.class);

            final List<ScoreJson> auditRule =
                    Arrays.asList(settingScoreRule.getManualScore(),
                            settingScoreRule.getPassScore(), settingScoreRule.getSuccessScore());
            auditResponse = super.audit(auditRule, bodyJson);
            return auditResponse;
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
        return auditResponse;

    }
}
