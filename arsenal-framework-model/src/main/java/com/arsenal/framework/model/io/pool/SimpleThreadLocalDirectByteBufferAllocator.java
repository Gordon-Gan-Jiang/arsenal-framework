package com.arsenal.framework.model.io.pool;

import com.google.common.base.Preconditions;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Gordon.Gan
 */
public class SimpleThreadLocalDirectByteBufferAllocator extends AbstractThreadLocalArrayAllocator<ByteBuffer> {
    private int count = 0;

    public SimpleThreadLocalDirectByteBufferAllocator(int maxCount, int bufferSize) {
        super(maxCount, bufferSize);
    }

    @Override
    public ByteBuffer allocate() {
        ByteBufferAllocator.simpleDirectTotalCount.increment();
        if (count == 0) {
            ByteBufferAllocator.simpleDirectAllocateCount.increment();
            return ByteBuffer.allocateDirect(bufferSize);
        } else {
            count--;
            ByteBuffer buffer = (ByteBuffer) buffers[count];
            buffers[count] = null;
            return buffer;
        }
    }

    @Override
    public void release(ByteBuffer array) {
        Preconditions.checkState(array.capacity() == bufferSize);
        if (count < maxCount) {
            buffers[count++] = array;
            array.clear();
        } else {
            ByteBufferAllocator.simpleDirectDeallocateCount.increment();
        }
    }

    @Override
    public void release(List<ByteBuffer> arrays) {
        for (int i = 0; i < arrays.size(); i++) {
            ByteBuffer array = arrays.get(i);
            Preconditions.checkState(array.capacity() == bufferSize);
            if (count < maxCount) {
                buffers[count++] = array;
                array.clear();
            } else {
                ByteBufferAllocator.simpleDirectDeallocateCount.increment();
            }
        }
    }
}