package com.lartimes.tiktok.service.audit;

import com.lartimes.tiktok.constant.AuditMsgMap;
import com.lartimes.tiktok.constant.AuditStatus;
import com.lartimes.tiktok.exception.BaseException;
import com.lartimes.tiktok.model.audit.AuditResponse;
import com.lartimes.tiktok.model.audit.ResultChildJson;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.StringMap;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/23 14:21
 */
@Service
public class TextAuditService extends AbstractAuditService<String, AuditResponse> {
    // 文本内容审核 API 地址
    static String textUrl = "http://ai.qiniuapi.com/v3/text/censor";
    static String method = "POST";
    // 根据官方文档的示例来自定义请求体
    static String textBody = "{\n" +
            "    \"data\": {\n" +
            "        \"text\": \"%s\"\n" +
            "    },\n" +
            "    \"params\": {\n" +
            "        \"scenes\": [\n" +
            "            \"antispam\"\n" +
            "        ]\n" +
            "    }\n" +
            "}";


    @Override
    public AuditResponse audit(String text) {
        AuditResponse auditResponse = new AuditResponse();
        auditResponse.setAuditStatus(AuditStatus.SUCCESS); //0 初始审核成功
        if (!isNeedAudit()) {
            return auditResponse;
        }
//        进行审核
        String body = String.format(textBody, text);
        final String token = qiNiuConfig.getToken(textUrl, method, body, contentType);
        StringMap header = new StringMap();
        header.put("Host", "ai.qiniuapi.com");
        header.put("Authorization", token);
        header.put("Content-Type", contentType);
        Configuration cfg = new Configuration(Region.region2());
        final Client client = new Client(cfg);
        try {
            Response response = client.post(textUrl, body.getBytes(), header, contentType);
            final Map map = objectMapper.readValue(response.getInfo().split("\n")[2], Map.class);
            // 将 map 中 key 为 "result" 的值转换成 ResultChildJson.class 类型的对象
            final ResultChildJson resultChild = objectMapper.convertValue(map.get("result"), ResultChildJson.class);

            // 文本审核直接审核 suggestion ，如果返回结果中的 suggestion 不是 pass 的话，就是不通过
            if (!resultChild.getSuggestion().equals("pass")) {
                auditResponse.setAuditStatus(AuditStatus.PASS);//不通过
                resultChild.getScenes().getAntispam().getDetails().forEach(
                        detailsJson -> {
                            if (!"normal".equals(detailsJson.getLabel())) {
                                auditResponse.setMsg(AuditMsgMap.getInfo(detailsJson.getLabel()) + "\n");
                            }
                        }
                );
            }
        } catch (Exception e) {
            throw new BaseException(this.getClass() + ": 审核出现问题 info-> " + e.getMessage());
        }
        return auditResponse;

    }
}
