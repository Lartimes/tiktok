package com.lartimes.tiktok.model.audit;

import lombok.Data;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/23 16:55
 */
@Data
public class ResultJson {
    Integer code;
    String message;
    ResultChildJson result;
}
