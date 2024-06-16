package com.arsenal.framework.model.io.pool;

/**
 * @author Gordon.Gan
 */
import com.arsenal.framework.model.StartupArgs;
import com.arsenal.framework.model.thread.FastThreadLocal;

import java.util.concurrent.atomic.LongAdder;

public class CharArrayAllocator {
    private static final FastThreadLocal<SyncThreadLocalCharArrayAllocator> syncAllocators = new FastThreadLocal<>();
    private static final FastThreadLocal<SimpleThreadLocalCharArrayAllocator> simpleAllocators = new FastThreadLocal<>();

    public static SyncThreadLocalCharArrayAllocator getCurrentSync() {
        return syncAllocators.safeGet(() -> new SyncThreadLocalCharArrayAllocator(
                StartupArgs.threadLocalBufferCountPerThread,
                StartupArgs.threadLocalBufferSize
        ));
    }

    public static SimpleThreadLocalCharArrayAllocator getCurrentSimple() {
        return simpleAllocators.safeGet(() -> new SimpleThreadLocalCharArrayAllocator(
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

