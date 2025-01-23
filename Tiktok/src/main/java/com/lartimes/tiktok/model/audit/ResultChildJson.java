package com.lartimes.tiktok.model.audit;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/23 14:38
 */
@Data
@ToString
public class ResultChildJson implements Serializable {
    String suggestion;
    ScenesJson scenes;
}
