package com.lartimes.tiktok.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2025/1/12 18:46
 */
public class LocalCache {
    private static Map<String,Object> cache = new ConcurrentHashMap();

    public static void put(String key,Object val){
        cache.put(key,val);
    }

    public static Boolean containsKey(String key){
        if (key == null) return false;
        return cache.containsKey(key);
    }

    public static void rem(String key) {
        cache.remove(key);
    }
}

