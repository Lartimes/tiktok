package com.lartimes.tiktok.model.audit;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/23 14:40
 */
@Data
@ToString
public class CutsJson implements Serializable {
    List<DetailsJson> details;
    String suggestion;
    Long offset;
}
