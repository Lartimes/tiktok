package com.lartimes.tiktok.model.audit;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/23 14:39
 */

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class TypeJson implements Serializable {
    String suggestion;
    List<CutsJson> cuts;
    List<DetailsJson> details;
}
