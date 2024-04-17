package com.arsenal.framework.model.io.pool;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SyncThreadLocalByteArrayAllocator extends AbstractThreadLocalArrayAllocator<byte[]> {
    private final Lock lock = new ReentrantLock();
    private volatile int count = 0;

    public SyncThreadLocalByteArrayAllocator(int maxCount, int bufferSize) {
        super(maxCount, bufferSize);
    }

    @Override
    public byte[] allocate() {
        lock.lock();
        try {
            ByteArrayAllocator.syncTotalCount.increment();
            if (count == 0) {
                ByteArrayAllocator.syncAllocateCount.increment();
                return new byte[bufferSize];
            } else {
                count--;
                byte[] buffer = (byte[]) buffers[count];
                buffers[count] = null;
                return buffer;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void release(byte[] array) {
        Preconditions.checkState(array.length == bufferSize);
        lock.lock();
        try {
            if (count < maxCount) {
                buffers[count++] = array;
            } else {
                ByteArrayAllocator.syncDeallocateCount.increment();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void release(List<byte[]> arrays) {
        lock.lock();
        try {
            for (byte[] arr : arrays) {
                Preconditions.checkState(arr.length == bufferSize);
                if (count < maxCount) {
                    buffers[count++] = arr;
                } else {
                    ByteArrayAllocator.syncDeallocateCount.increment();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}

