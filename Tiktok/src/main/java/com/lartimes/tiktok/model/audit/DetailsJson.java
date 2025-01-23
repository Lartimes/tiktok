package com.lartimes.tiktok.model.audit;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/23 14:40
 */
@Data
@ToString
public class DetailsJson implements Serializable {
    Double score;
    String suggestion;
    String label;
    String group;
}
