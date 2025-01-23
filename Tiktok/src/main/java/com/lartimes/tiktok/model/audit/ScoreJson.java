package com.lartimes.tiktok.model.audit;

import lombok.Data;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/23 16:57
 */
@Data
public class ScoreJson {
    Double minPulp;
    Double maxPulp;

    Double minTerror;
    Double maxTerror;

    Double minPolitician;
    Double maxPolitician;

    Integer auditStatus;
}
