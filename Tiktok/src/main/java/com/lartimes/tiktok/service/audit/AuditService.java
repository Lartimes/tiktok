package com.lartimes.tiktok.service.audit;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/23 13:34
 */
public interface AuditService<T, R> {
    /**
     * 审核规范
     *
     * @param task
     * @return
     */
    R audit(T task);
}
