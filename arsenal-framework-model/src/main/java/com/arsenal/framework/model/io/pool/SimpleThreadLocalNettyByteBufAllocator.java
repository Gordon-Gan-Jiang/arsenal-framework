package com.arsenal.framework.model.io.pool;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Gordon.Gan
 */
@Setter
@Getter
public class SimpleThreadLocalNettyByteBufAllocator extends AbstractThreadLocalArrayAllocator<ByteBuf> {
    private Integer maxCount;
    private Integer bufferSize;
    private volatile Integer count = 0;

    public SimpleThreadLocalNettyByteBufAllocator(Integer maxCount, Integer bufferSize) {
        super(maxCount, bufferSize);
        this.maxCount = maxCount;
        this.bufferSize = bufferSize;
    }

    @Override
    public ByteBuf allocate() {
        NettyByteBufAllocator.simpleTotalCount.increment();
        if (count == 0) {
            NettyByteBufAllocator.simpleAllocateCount.increment();
            return PooledByteBufAllocator.DEFAULT.directBuffer(bufferSize, bufferSize);
        } else {
            count--;
            ByteBuf buffer = (ByteBuf) buffers[count];
            buffers[count] = null;
            return buffer;
        }
    }

    @Override
    public void release(ByteBuf array) {
        if (count < maxCount) {
            array.clear();
            buffers[count++] = array;
            return;
        }
        NettyByteBufAllocator.simpleDeallocateCount.increment();
        array.release();
    }

    @Override
    public void release(List<ByteBuf> arrays) {
        arrays.forEach(it->{
            if (count<maxCount) {
                it.clear();
                buffers[count++] =it;
            } else {
                NettyByteBufAllocator.simpleDeallocateCount.increment();
                it.release();
            }
        });
    }
}
