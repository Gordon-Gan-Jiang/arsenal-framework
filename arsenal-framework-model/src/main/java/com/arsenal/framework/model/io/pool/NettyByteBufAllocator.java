package com.arsenal.framework.model.io.pool;

import com.arsenal.framework.model.StartupArgs;
import com.arsenal.framework.model.thread.FastThreadLocal;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author Gordon.Gan
 */
public class NettyByteBufAllocator {
    private static FastThreadLocal<SimpleThreadLocalNettyByteBufAllocator> simpleAllocators = new FastThreadLocal<>();
    private static FastThreadLocal<SyncThreadLocalNettyByteBufAllocator> syncAllocators = new FastThreadLocal<>();

    public static SimpleThreadLocalNettyByteBufAllocator getCurrentSimple() {
        return simpleAllocators.safeGet(() ->
                new SimpleThreadLocalNettyByteBufAllocator(
                        StartupArgs.threadLocalBufferCountPerThread,
                        StartupArgs.threadLocalBufferSize
                )
        );
    }

    public static SyncThreadLocalNettyByteBufAllocator getCurrentSync() {
        return syncAllocators.safeGet(() ->
                new SyncThreadLocalNettyByteBufAllocator(
                        StartupArgs.threadLocalBufferCountPerThread,
                        StartupArgs.threadLocalBufferSize
                )
        );
    }

    public static LongAdder simpleAllocateCount = new LongAdder();
    public static LongAdder simpleDeallocateCount = new LongAdder();
    public static LongAdder simpleTotalCount = new LongAdder();
    public static LongAdder syncAllocateCount = new LongAdder();
    public static LongAdder syncDeallocateCount = new LongAdder();
    public static LongAdder syncTotalCount = new LongAdder();
}
