package com.arsenal.framework.model.io.pool;
import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Gordon.Gan
 */


public class SimpleThreadLocalHeapByteBufferAllocator extends AbstractThreadLocalArrayAllocator<ByteBuffer> {
    private int count = 0;

    public SimpleThreadLocalHeapByteBufferAllocator(int maxCount, int bufferSize) {
        super(maxCount, bufferSize);
    }

    @Override
    public ByteBuffer allocate() {
        ByteBufferAllocator.simpleHeapTotalCount.increment();
        if (count == 0) {
            ByteBufferAllocator.simpleHeapAllocateCount.increment();
            return ByteBuffer.allocate(bufferSize);
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
            ByteBufferAllocator.simpleHeapDeallocateCount.increment();
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
                ByteBufferAllocator.simpleHeapDeallocateCount.increment();
            }
        }
    }
}
