package com.lartimes.tiktok.model.audit;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/23 14:39
 */
@Data
@ToString
public class ScenesJson implements Serializable {
    private TypeJson terror;
    private TypeJson politician;
    private TypeJson pulp;
    private TypeJson antispam;

}
