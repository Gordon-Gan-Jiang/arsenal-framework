package com.arsenal.framework.model.io;

import com.arsenal.framework.model.io.pool.AbstractThreadLocalArrayAllocator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gordon.Gan
 */
@Getter
public class BaseMultiArrayBuffer<T> implements AutoCloseable {
    protected AbstractThreadLocalArrayAllocator<T> allocator;

    public BaseMultiArrayBuffer(AbstractThreadLocalArrayAllocator<T> allocator) {
        this.allocator = allocator;
    }

    protected List<T> buffers = new ArrayList<>();

    protected T currentBuffer = newBuffer();

    protected int position = 0;

    protected volatile Integer length = 0;

    @Override
    public void close() {
        allocator.release(buffers);
        buffers.clear();
        position = 0;
    }

    protected T newBuffer() {
        T buffer = allocator.allocate();
        position = 0;

        buffers.add(buffer);
        return buffer;
    }
}
