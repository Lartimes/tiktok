package com.lartimes.tiktok.model.vo;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/8 13:53
 */
@Data
public class FavoritesVo {
    @Nullable
    @NumberFormat
    private Integer id;
    @NotNull(message = "必须指定名字")
    private String name;
    @Nullable
    private String description;
}
