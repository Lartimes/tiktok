package com.lartimes.tiktok.model.vo;

import lombok.Data;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/2/2 15:50
 */
@Data
public class Model {
    private String label;
    private Long videoId;
    /**
     * 暴漏的接口只有根据停留时长 {@link com.lartimes.tiktok.controller.CustomerController#updateUserModel}
     */
    private Double score;
}
