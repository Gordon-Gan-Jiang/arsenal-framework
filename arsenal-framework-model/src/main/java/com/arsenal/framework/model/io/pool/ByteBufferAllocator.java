package com.arsenal.framework.model.io.pool;

import com.arsenal.framework.model.StartupArgs;
import com.arsenal.framework.model.thread.FastThreadLocal;

import java.util.concurrent.atomic.LongAdder;

public class ByteBufferAllocator {
    private static final FastThreadLocal<SyncThreadLocalDirectByteBufferAllocator> syncDirectBufferAllocators = new FastThreadLocal<>();
    private static final FastThreadLocal<SimpleThreadLocalDirectByteBufferAllocator> simpleDirectBufferAllocators = new FastThreadLocal<>();
    private static final FastThreadLocal<SyncThreadLocalHeapByteBufferAllocator> syncHeapBufferAllocators = new FastThreadLocal<>();
    private static final FastThreadLocal<SimpleThreadLocalHeapByteBufferAllocator> simpleHeapBufferAllocators = new FastThreadLocal<>();

    public static SyncThreadLocalDirectByteBufferAllocator getCurrentDirectSync() {
        return syncDirectBufferAllocators.safeGet(() -> new SyncThreadLocalDirectByteBufferAllocator(
                StartupArgs.threadLocalBufferCountPerThread,
                StartupArgs.threadLocalBufferSize
        ));
    }

    public static SimpleThreadLocalDirectByteBufferAllocator getCurrentDirectSimple() {
        return simpleDirectBufferAllocators.safeGet(() -> new SimpleThreadLocalDirectByteBufferAllocator(
                StartupArgs.threadLocalBufferCountPerThread,
                StartupArgs.threadLocalBufferSize
        ));
    }

    public static SyncThreadLocalHeapByteBufferAllocator getCurrentHeapSync() {
        return syncHeapBufferAllocators.safeGet(() -> new SyncThreadLocalHeapByteBufferAllocator(
                StartupArgs.threadLocalBufferCountPerThread,
                StartupArgs.threadLocalBufferSize
        ));
    }

    public static SimpleThreadLocalHeapByteBufferAllocator getCurrentHeapSimple() {
        return simpleHeapBufferAllocators.safeGet(() -> new SimpleThreadLocalHeapByteBufferAllocator(
                StartupArgs.threadLocalBufferCountPerThread,
                StartupArgs.threadLocalBufferSize
        ));
    }

    public static final LongAdder syncDirectAllocateCount = new LongAdder();
    public static final LongAdder syncDirectDeallocateCount = new LongAdder();
    public static final LongAdder syncDirectTotalCount = new LongAdder();
    public static final LongAdder simpleDirectAllocateCount = new LongAdder();
    public static final LongAdder simpleDirectDeallocateCount = new LongAdder();
    public static final LongAdder simpleDirectTotalCount = new LongAdder();
    public static final LongAdder syncHeapAllocateCount = new LongAdder();
    public static final LongAdder syncHeapDeallocateCount = new LongAdder();
    public static final LongAdder syncHeapTotalCount = new LongAdder();
    public static final LongAdder simpleHeapAllocateCount = new LongAdder();
    public static final LongAdder simpleHeapDeallocateCount = new LongAdder();
    public static final LongAdder simpleHeapTotalCount = new LongAdder();
}

