package com.arsenal.framework.model.io.pool;

/**
 * @author Gordon.Gan
 */
import com.google.common.base.Preconditions;

import java.util.List;

public class SimpleThreadLocalCharArrayAllocator extends AbstractThreadLocalArrayAllocator<char[]> {
    private int count = 0;

    public SimpleThreadLocalCharArrayAllocator(int maxCount, int bufferSize) {
        super(maxCount, bufferSize);
    }

    @Override
    public char[] allocate() {
        CharArrayAllocator.simpleTotalCount.increment();
        if (count == 0) {
            CharArrayAllocator.simpleAllocateCount.increment();
            return new char[bufferSize];
        } else {
            count -= 1;
            char[] buffer = (char[]) buffers[count];
            buffers[count] = null;
            return buffer;
        }
    }

    @Override
    public void release(char[] array) {
        Preconditions.checkState(array.length == bufferSize);
        if (count < maxCount) {
            buffers[count++] = array;
        } else {
            CharArrayAllocator.simpleDeallocateCount.increment();
        }
    }

    @Override
    public void release(List<char[]> arrays) {
        for (char[] array : arrays) {
            Preconditions.checkState(array.length == bufferSize);
            if (count < maxCount) {
                buffers[count++] = array;
            } else {
                CharArrayAllocator.simpleDeallocateCount.increment();
            }
        }
    }
}

