package com.lartimes.tiktok.model.audit;

import lombok.Data;
import lombok.ToString;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/23 14:23
 */
@Data
@ToString
public class AuditResponse {
    private Integer auditStatus;
    // true:正常 false:违规
    private Boolean legal;
    // 信息
    private String msg;

    private Long offset;

    public AuditResponse(Integer auditStatus, String msg) {
        this.auditStatus = auditStatus;
        this.msg = msg;
    }

    public AuditResponse() {
    }
}
