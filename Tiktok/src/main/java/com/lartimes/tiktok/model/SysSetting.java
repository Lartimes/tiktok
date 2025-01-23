package com.lartimes.tiktok.model;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author lartimes
 */
@Data
@TableName("sys_setting")
public class SysSetting implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String auditPolicy;

    private Double hotLimit;

    /**
     * 审核开关,0为关，1为开
     */
    private Integer auditOpen;

    private String allowIp;

    /**
     * 回源鉴权开关,0关闭，1开启,默认为1
     */
    private Integer auth;


}
