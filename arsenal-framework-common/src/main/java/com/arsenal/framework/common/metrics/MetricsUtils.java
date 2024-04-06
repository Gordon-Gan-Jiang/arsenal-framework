package com.arsenal.framework.common.metrics;

import com.arsenal.framework.model.Logging;
import com.arsenal.framework.model.collection.Pair;

import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * @author Gordon.Gan
 */

public class MetricsUtils extends Logging{
    public static final String API_METRICS_CATEGORY = "API";
    public static final String API_PATH_CATEGORY = "API_PATH";
    public static final String API_STATUS_METRICS_CATEGORY = "API_STATUS";
    public static final String API_SLOW_METRICS_CATEGORY = "API_SLOW";
    public static final String ALARM_CATEGORY = "ALARM";
    public static final String RPC_CLIENT_METRICS_CATEGORY = "RPC_CLIENT";
    public static final String API_SLOW_PROCESS_METRICS_CATEGORY = "API_SLOW_PROCESS";
    public static final String DB_METRICS_CATEGORY = "DB";
    public static final String SQS_METRICS_CATEGORY = "SQS";
    public static final String MSG_METRICS_CATEGORY = "MSG";
    public static final String SCHEDULER_METRICS_CATEGORY = "SCHEDULER";
    public static final String CACHE_METRICS_CATEGORY = "CACHE";
    public static final String QUEUE_METRICS_CATEGORY = "QUEUE";
    public static final String COROUTINE_TIME_CATEGORY = "COROUTINE_TIME";
    public static final String EXCEPTION_SOURCE_CATEGORY = "EXCEPTION_SRC";

    public static final String QPS_METRICS = "QPS";
    public static final String MEDIAN_METRICS = "MEDIAN";
    public static final String FIFTY_PERCENT_METRICS = "50%";
    public static final String NINETY_PERCENT_METRICS = "90%";
    public static final String NINETY_FIVE_PERCENT_METRICS = "95%";
    public static final String NINETY_EIGHT_PERCENT_METRICS = "98%";
    public static final String MAX_METRICS = "MAX";
    public static final String MIN_METRICS = "MIN";
    public static final String AVERAGE_METRICS = "AVERAGE";
    public static final String COUNT_METRICS = "COUNT";
    public static final String SUM_METRICS = "SUM";

    public static final String SUCCESS_SUFFIX = "success";
    public static final String DOT_SUCCESS_SUFFIX = ".success";
    public static final String FAIL_SUFFIX = "fail";
    public static final String DOT_FAIL_SUFFIX = ".fail";
    public static final String FUSED_SUFFIX = "fuse";
    public static final String DOT_FUSED_SUFFIX = ".fuse";
    public static final String CANCEL_SUFFIX = "cancel";
    public static final String DOT_CANCEL_SUFFIX = ".cancel";

    public static final String RPC_CLIENT_SYNC_SUCCESS_SUFFIX = "sync." + SUCCESS_SUFFIX;
    public static final String DOT_RPC_CLIENT_SYNC_SUCCESS_SUFFIX = ".sync." + SUCCESS_SUFFIX;
    public static final String RPC_CLIENT_SYNC_FAIL_SUFFIX = "sync." + FAIL_SUFFIX;
    public static final String DOT_RPC_CLIENT_SYNC_FAIL_SUFFIX = ".sync." + FAIL_SUFFIX;

    public static final String RPC_CLIENT_ASYNC_SUCCESS_SUFFIX = "async." + SUCCESS_SUFFIX;
    public static final String DOT_RPC_CLIENT_ASYNC_SUCCESS_SUFFIX = ".async." + SUCCESS_SUFFIX;
    public static final String RPC_CLIENT_ASYNC_FAIL_SUFFIX = "async." + FAIL_SUFFIX;
    public static final String DOT_RPC_CLIENT_ASYNC_FAIL_SUFFIX = ".async." + FAIL_SUFFIX;

    public static final String RPC_CLIENT_ASYNC_CALLBACK_SUCCESS_SUFFIX = "async_callback." + SUCCESS_SUFFIX;
    public static final String DOT_RPC_CLIENT_ASYNC_CALLBACK_SUCCESS_SUFFIX = ".async_callback." + SUCCESS_SUFFIX;
    public static final String RPC_CLIENT_ASYNC_CALLBACK_FAIL_SUFFIX = "async_callback." + FAIL_SUFFIX;
    public static final String DOT_RPC_CLIENT_ASYNC_CALLBACK_FAIL_SUFFIX = ".async_callback." + FAIL_SUFFIX;
    public static final String RPC_CLIENT_ASYNC_CALLBACK_CANCEL_SUFFIX = "async_callback." + CANCEL_SUFFIX;
    public static final String DOT_RPC_CLIENT_ASYNC_CALLBACK_CANCEL_SUFFIX = ".async_callback." + CANCEL_SUFFIX;

    public static final ConcurrentLinkedQueue<Pair<String, WeakReference<MetricsLatencyBuffer>>> ALL_THREAD_LATENCY_BUFFER =
            new ConcurrentLinkedQueue<>();
    public static final ConcurrentLinkedQueue<Pair<String, WeakReference<MetricsSummaryBuffer>>> ALL_THREAD_SUMMARY_BUFFER =
            new ConcurrentLinkedQueue<>();

    public static final ThreadLocal<Map<MetricsKey, MetricsLatencyBuffer>> THREAD_LATENCY_BUFFER_MAP = new ThreadLocal<>();
    public static final ThreadLocal<Map<MetricsKey, MetricsSummaryBuffer>> THREAD_SUMMARY_BUFFER_MAP = new ThreadLocal<>();

    public static final com.github.benmanes.caffeine.cache.Cache<String, String> SLOW_SQL_CACHE = com.github.benmanes.caffeine.cache.Caffeine.newBuilder()
            .maximumSize(200)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();

    public static List<MetricsLatencyBuffer> getAllLatencyBuffers() {
        cleanWeakReferences(ALL_THREAD_LATENCY_BUFFER);
        return ALL_THREAD_LATENCY_BUFFER.stream()
                .map(pair -> pair.getValue().get())
                .filter(buffer -> buffer != null)
                .collect(Collectors.toList());
    }

    public static List<MetricsSummaryBuffer> getAllSummaryBuffers() {
        cleanWeakReferences(ALL_THREAD_SUMMARY_BUFFER);
        return ALL_THREAD_SUMMARY_BUFFER.stream()
                .map(pair -> pair.getValue().get())
                .filter(buffer -> buffer != null)
                .collect(Collectors.toList());
    }

    public static void recordLatency(String category, String name, String subName, int runningTime) {
        if (runningTime < 0) {
            System.err.println("invalid running time: " + runningTime + ".");
            return;
        }
        MetricsKey metricsKey = new MetricsKey(category, name, subName);
        queryThreadLatencyBuffer(metricsKey).getLatencyBuffer().add(runningTime);
        queryThreadSummaryBuffer(metricsKey).getSummaryBuffer().increment(runningTime);
    }

    public static void recordCounter(String category, String name) {
        recordCounter(category, name, "");
    }

    public static void recordCounter(String category, String name, String subName) {
        queryThreadSummaryBuffer(new MetricsKey(category, name, subName)).getSummaryBuffer().increment();
    }

    public static void recordAlarm(String name) {
        recordCounter(ALARM_CATEGORY, name);
    }

    public static MetricsLatencyBuffer queryThreadLatencyBuffer(MetricsKey metricsKey) {
        Map<MetricsKey, MetricsLatencyBuffer> map = THREAD_LATENCY_BUFFER_MAP.get();
        if (map == null) {
            map = new ConcurrentHashMap<>();
            THREAD_LATENCY_BUFFER_MAP.set(map);
        }
        return map.computeIfAbsent(metricsKey, key -> {
            MetricsLatencyBuffer buffer = new MetricsLatencyBuffer(key, new LatencyBuffer());
            ALL_THREAD_LATENCY_BUFFER.add(new Pair<>(Thread.currentThread().getName(), new WeakReference<>(buffer)));
            return buffer;
        });
    }

    public static MetricsSummaryBuffer queryThreadSummaryBuffer(MetricsKey metricsKey) {
        Map<MetricsKey, MetricsSummaryBuffer> map = THREAD_SUMMARY_BUFFER_MAP.get();
        if (map == null) {
            map = new ConcurrentHashMap<>();
            THREAD_SUMMARY_BUFFER_MAP.set(map);
        }
        return map.computeIfAbsent(metricsKey, key -> {
            MetricsSummaryBuffer buffer = new MetricsSummaryBuffer(key, new SummaryBuffer());
            ALL_THREAD_SUMMARY_BUFFER.add(new Pair<>(Thread.currentThread().getName(), new WeakReference<>(buffer)));
            return buffer;
        });
    }

    public static <T> void cleanWeakReferences(ConcurrentLinkedQueue<Pair<String, WeakReference<T>>> references) {
        List<String> threadNames = new ArrayList<>();
        Iterator<Pair<String, WeakReference<T>>> iterator = references.iterator();
        while (iterator.hasNext()) {
            Pair<String, WeakReference<T>> pair = iterator.next();
            if (pair.getValue() == null) {
                threadNames.add(pair.getKey());
                iterator.remove();
            }
        }

        if (!threadNames.isEmpty()) {
            log.error("some metrics is removed in " + String.join(",", threadNames) + ".");
        }
    }

}
