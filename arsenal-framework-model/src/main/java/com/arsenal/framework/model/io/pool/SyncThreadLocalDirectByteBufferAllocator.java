package com.arsenal.framework.model.io.pool;

// Copyright 2020 ALO7 Inc. All rights reserved.

import com.arsenal.framework.model.locks.SpinLock;
import com.google.common.base.Preconditions;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Yue Jun(jun.yue@alo7.com)
 */
public class SyncThreadLocalDirectByteBufferAllocator extends AbstractThreadLocalArrayAllocator<ByteBuffer> {
    private final SpinLock lock = new SpinLock();
    private volatile int count = 0;

    public SyncThreadLocalDirectByteBufferAllocator(int maxCount, int bufferSize) {
        super(maxCount, bufferSize);
    }

    @Override
    public ByteBuffer allocate() {
        lock.use(() -> {
            ByteBufferAllocator.syncDirectTotalCount.increment();
            if (count == 0) {
                ByteBufferAllocator.syncDirectAllocateCount.increment();
                return ByteBuffer.allocateDirect(bufferSize);
            } else {
                count--;
                ByteBuffer buffer = (ByteBuffer) buffers[count];
                buffers[count] = null;
                return buffer;
            }
        });
        return null;
    }

    @Override
    public void release(ByteBuffer array) {
        Preconditions.checkState(array.capacity() == bufferSize);
        lock.use(() -> {
            if (count < maxCount) {
                buffers[count++] = array;
                array.clear();
            } else {
                ByteBufferAllocator.syncDirectDeallocateCount.increment();
            }
            return null;
        });

    }

    @Override
    public void release(List<ByteBuffer> arrays) {
        lock.use(() -> {
            for (int i = 0; i < arrays.size(); i++) {
                ByteBuffer array = arrays.get(i);
                Preconditions.checkState(array.capacity() == bufferSize);
                if (count < maxCount) {
                    buffers[count++] = array;
                    array.clear();
                } else {
                    ByteBufferAllocator.syncDirectDeallocateCount.increment();
                }
            }
            return null;
        });

    }
}

