package com.arsenal.framework.model.io.pool;

import com.google.common.base.Preconditions;

import java.util.List;

public class SimpleThreadLocalByteArrayAllocator extends AbstractThreadLocalArrayAllocator<byte[]> {
    private int count = 0;

    public SimpleThreadLocalByteArrayAllocator(int maxCount, int bufferSize) {
        super(maxCount, bufferSize);
    }

    @Override
    public byte[] allocate() {
        ByteArrayAllocator.simpleTotalCount.increment();
        if (count == 0) {
            ByteArrayAllocator.simpleAllocateCount.increment();
            return new byte[bufferSize];
        } else {
            count--;
            byte[] buffer = (byte[]) buffers[count];
            buffers[count] = null;
            return buffer;
        }
    }

    @Override
    public void release(byte[] array) {
        Preconditions.checkState(array.length == bufferSize);
        if (count < maxCount) {
            buffers[count++] = array;
        } else {
            ByteArrayAllocator.simpleDeallocateCount.increment();
        }
    }

    @Override
    public void release(List<byte[]> arrays) {
        for (byte[] arr : arrays) {
            Preconditions.checkState(arr.length == bufferSize);
            if (count < maxCount) {
                buffers[count++] = arr;
            } else {
                ByteArrayAllocator.simpleDeallocateCount.increment();
            }
        }
    }
}

