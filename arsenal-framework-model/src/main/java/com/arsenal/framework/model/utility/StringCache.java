package com.arsenal.framework.model.utility;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Gordon.Gan
 */
public class StringCache {
    private static final AtomicReference<ConcurrentHashMap<String, AtomicReference<ConcurrentHashMap<String, String>>>> cache =
            new AtomicReference<>(new ConcurrentHashMap<>());

    public static String queryConcat(String first, String second) {
        ConcurrentHashMap<String, String> secondMap = cache.get().computeIfAbsent(first, k -> new AtomicReference<>(new ConcurrentHashMap<>())).get();
        return secondMap.computeIfAbsent(second, k -> first + second);
    }

    public static String queryConcat(String first, String second, String third) {
        return queryConcat(queryConcat(first, second), third);
    }

    public static String queryConcat(String first, String second, String third, String forth) {
        return queryConcat(queryConcat(first, second, third), forth);
    }
}
