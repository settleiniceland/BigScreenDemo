package com.iwip.harbor.task;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class InMemoryTaskLogStore {
    // key: 任务唯一标识（比如泊位 ID、任务名）
    // value: 最新日志
    private static final Map<String, String> logCache = new ConcurrentHashMap<>();
    // 写入或更新
    public static void put(String key, String value) {
        logCache.put(key, value);
    }
    // 读取单个
    public static String get(String key) {
        return logCache.get(key);
    }
    // 读取全部
    public static Map<String, String> getAll() {
        return logCache;
    }
    // 清空（可选）
    public static void clear() {
        logCache.clear();
    }
}

