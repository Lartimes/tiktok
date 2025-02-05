package com.lartimes.tiktok.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/2/5 23:04
 */
@Data
public class ModelVO {

    private Long userId;
    // 兴趣视频分类
    private List<String> labels;
}
