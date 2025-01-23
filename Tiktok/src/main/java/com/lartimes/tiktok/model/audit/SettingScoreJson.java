package com.lartimes.tiktok.model.audit;

import lombok.Data;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/23 16:57
 */
@Data
public class SettingScoreJson {
    // 通过
    ScoreJson successScore;
    // 人工审核
    ScoreJson manualScore;
    // PASS
    ScoreJson passScore;
}
