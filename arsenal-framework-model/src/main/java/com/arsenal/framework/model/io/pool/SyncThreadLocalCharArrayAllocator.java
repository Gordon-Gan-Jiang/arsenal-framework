package com.arsenal.framework.model.io.pool;

/**
 * @author Gordon.Gan
 */
import com.arsenal.framework.model.locks.SpinLock;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SyncThreadLocalCharArrayAllocator extends AbstractThreadLocalArrayAllocator<char[]> {
    private final SpinLock lock = new SpinLock();
    private volatile int count = 0;

    public SyncThreadLocalCharArrayAllocator(int maxCount, int bufferSize) {
        super(maxCount, bufferSize);
    }

    @Override
    public char[] allocate() {
        lock.lock();
        try {
            CharArrayAllocator.syncTotalCount.increment();
            if (count == 0) {
                CharArrayAllocator.syncAllocateCount.increment();
                return new char[bufferSize];
            } else {
                count -= 1;
                char[] buffer = (char[]) buffers[count];
                buffers[count] = null;
                return buffer;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void release(char[] array) {
        Preconditions.checkState(array.length == bufferSize);
        lock.lock();
        try {
            if (count < maxCount) {
                buffers[count++] = array;
            } else {
                CharArrayAllocator.syncDeallocateCount.increment();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void release(List<char[]> arrays) {
        lock.lock();
        try {
            for (char[] array : arrays) {
                Preconditions.checkState(array.length == bufferSize);
                if (count < maxCount) {
                    buffers[count++] = array;
                } else {
                    CharArrayAllocator.syncDeallocateCount.increment();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}

