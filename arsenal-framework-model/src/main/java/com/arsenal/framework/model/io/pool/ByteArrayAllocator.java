package com.arsenal.framework.model.io.pool;

import com.arsenal.framework.model.StartupArgs;
import com.arsenal.framework.model.thread.FastThreadLocal;

import java.util.concurrent.atomic.LongAdder;

public class ByteArrayAllocator {
    private static final FastThreadLocal<SyncThreadLocalByteArrayAllocator> syncAllocators = new FastThreadLocal<>();
    private static final FastThreadLocal<SimpleThreadLocalByteArrayAllocator> simpleAllocators = new FastThreadLocal<>();

    public static SyncThreadLocalByteArrayAllocator getCurrentSync() {
        return syncAllocators.safeGet(() -> new SyncThreadLocalByteArrayAllocator(
                StartupArgs.threadLocalBufferCountPerThread,
                StartupArgs.threadLocalBufferSize
        ));
    }

    public static SimpleThreadLocalByteArrayAllocator getCurrentSimple() {
        return simpleAllocators.safeGet(() -> new SimpleThreadLocalByteArrayAllocator(
                StartupArgs.threadLocalBufferCountPerThread,
                StartupArgs.threadLocalBufferSize
        ));
    }

    public static final LongAdder syncAllocateCount = new LongAdder();
    public static final LongAdder syncDeallocateCount = new LongAdder();
    public static final LongAdder syncTotalCount = new LongAdder();
    public static final LongAdder simpleAllocateCount = new LongAdder();
    public static final LongAdder simpleDeallocateCount = new LongAdder();
    public static final LongAdder simpleTotalCount = new LongAdder();
}

