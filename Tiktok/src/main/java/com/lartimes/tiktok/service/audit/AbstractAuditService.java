package com.lartimes.tiktok.service.audit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lartimes.tiktok.config.LocalCache;
import com.lartimes.tiktok.config.QiNiuConfig;
import com.lartimes.tiktok.constant.AuditMsgMap;
import com.lartimes.tiktok.constant.AuditStatus;
import com.lartimes.tiktok.model.audit.*;
import com.lartimes.tiktok.model.SysSetting;
import com.lartimes.tiktok.service.SysSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/23 13:40
 */
@Service
public abstract class AbstractAuditService<T, R> implements AuditService<T, R> {

    static final String contentType = "application/json";
    @Autowired
    protected QiNiuConfig qiNiuConfig;
    @Autowired
    protected SysSettingService settingService;
    protected ObjectMapper objectMapper = new ObjectMapper().
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    /**
     * 根据系统配置表查询是否需要审核
     *
     * @return
     */
    protected Boolean isNeedAudit() {
        final SysSetting setting = settingService.list().get(0);
        return setting.getAuditOpen() == 1;
    }

    protected String appendUUID(String url) {

        final SysSetting setting = settingService.list().get(0);

        if (setting.getAuth() == 1) {
            final String uuid = UUID.randomUUID().toString();
            LocalCache.put(uuid, true);
            if (url.contains("?")) {
                url = url + "&uuid=" + uuid;
            } else {
                url = url + "?uuid=" + uuid;
            }
            return url;
        }
        return url;
    }

    protected AuditResponse audit(List<ScoreJson> scoreJsonList, BodyJson bodyJson) {
        AuditResponse audit = new AuditResponse();
        for (ScoreJson scoreJson : scoreJsonList) {
            audit = doAudit(scoreJson, bodyJson);
            if (audit.getLegal()) {
                audit.setAuditStatus(scoreJson.getAuditStatus());
                return audit;
            }
        }
        final ScenesJson scenes = bodyJson.getResult().getResult().getScenes();
        if (endCheck(scenes)) {
            audit.setAuditStatus(AuditStatus.SUCCESS);
        } else {
            audit.setAuditStatus(AuditStatus.PASS);
            audit.setMsg("内容不合法");
        }
        return audit;

    }

    private AuditResponse doAudit(ScoreJson scoreJson, BodyJson bodyJson) {

        AuditResponse auditResponse = new AuditResponse();
        auditResponse.setLegal(false);
        auditResponse.setAuditStatus(scoreJson.getAuditStatus());

        final Double minPolitician = scoreJson.getMinPolitician();
        final Double maxPolitician = scoreJson.getMaxPolitician();
        final Double minPulp = scoreJson.getMinPulp();
        final Double maxPulp = scoreJson.getMaxPulp();
        final Double minTerror = scoreJson.getMinTerror();
        final Double maxTerror = scoreJson.getMaxTerror();

        // 所有都要比较,如果返回的有问题则直接返回
        if (!ObjectUtils.isEmpty(bodyJson.getPolitician())) {
            if (bodyJson.checkViolation(bodyJson.getPolitician(), minPolitician, maxPolitician)) {
                final AuditResponse response = getInfo(bodyJson.getPolitician(), minPolitician, "group");
                auditResponse.setMsg(response.getMsg());
                if (!response.getLegal()) {
                    auditResponse.setOffset(response.getOffset());
                    return auditResponse;
                }
            }
        }
        if (!ObjectUtils.isEmpty(bodyJson.getPulp())) {
            if (bodyJson.checkViolation(bodyJson.getPulp(), minPulp, maxPulp)) {
                final AuditResponse response = getInfo(bodyJson.getPulp(), minPulp, "normal");
                auditResponse.setMsg(response.getMsg());
                // 如果违规则提前返回
                if (!response.getLegal()) {
                    auditResponse.setOffset(response.getOffset());
                    return auditResponse;
                }
            }
        }
        if (!ObjectUtils.isEmpty(bodyJson.getTerror())) {
            if (bodyJson.checkViolation(bodyJson.getTerror(), minTerror, maxTerror)) {
                final AuditResponse response = getInfo(bodyJson.getTerror(), minTerror, "normal");
                auditResponse.setMsg(response.getMsg());
                if (!response.getLegal()) {
                    auditResponse.setOffset(response.getOffset());
                    return auditResponse;
                }
            }
        }
        auditResponse.setMsg("正常");
        auditResponse.setLegal(true);
        return auditResponse;
    }

    private AuditResponse getInfo(List<CutsJson> types, Double minValue, String key) {
        AuditResponse auditResponse = new AuditResponse();
        auditResponse.setLegal(true); //
        String info = null;
        // 获取信息
        for (CutsJson type : types) {
            for (DetailsJson detail : type.getDetails()) {
                if (detail.getScore() > minValue) {
                    if (!detail.getLabel().equals(key)) {
                        info = AuditMsgMap.getInfo(detail.getLabel());
                        auditResponse.setMsg(info);
                        auditResponse.setOffset(type.getOffset());
                    }
                    auditResponse.setLegal(false);
                }
            }
        }
        if (!auditResponse.getLegal() && ObjectUtils.isEmpty(auditResponse.getMsg())) {
            auditResponse.setMsg("该视频违法tiktok平台规则");
        }
        return auditResponse;

    }

    private boolean endCheck(ScenesJson scenes) {
        final TypeJson terror = scenes.getTerror();
        final TypeJson politician = scenes.getPolitician();
        final TypeJson pulp = scenes.getPulp();
        return !terror.getSuggestion().equals("block") &&
                !politician.getSuggestion().equals("block") && !pulp.getSuggestion().equals("block");
    }
}
