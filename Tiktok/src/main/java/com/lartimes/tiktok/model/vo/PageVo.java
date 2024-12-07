package com.lartimes.tiktok.model.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/5 10:11
 */
@Data
public class PageVo {

    private final Long page = 1L;
    private final Long limit = 15L;

    public IPage page() {
        return new Page(this.page, this.limit);
    }
}
