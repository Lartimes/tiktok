package com.lartimes.tiktok.holder;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/2 21:44
 */
public class UserHolder {
    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();


    public static void set(Object id) {
        THREAD_LOCAL.set(Long.parseLong(id.toString()));
    }

    public static Long get() {
        return THREAD_LOCAL.get();
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
