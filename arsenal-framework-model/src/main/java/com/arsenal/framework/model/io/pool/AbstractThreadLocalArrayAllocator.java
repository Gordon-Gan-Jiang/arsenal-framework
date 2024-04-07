package com.arsenal.framework.model.io.pool;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Gordon.Gan
 */
public abstract class AbstractThreadLocalArrayAllocator<T> {
    public AbstractThreadLocalArrayAllocator(Integer maxCount, Integer bufferSize) {
        this.maxCount = maxCount;
        this.bufferSize = bufferSize;
        this.buffers = new Object[maxCount];

    }

    @Setter
    @Getter
    protected Integer maxCount;

    @Setter
    @Getter
    protected Integer bufferSize;

    @Setter
    @Getter
    protected Object [] buffers;

    public abstract T allocate();

    public abstract void release(T array);

    public abstract void release(List<T> arrays);
}
