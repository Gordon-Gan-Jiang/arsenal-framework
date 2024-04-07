package com.arsenal.framework.model.io.pool;

import com.arsenal.framework.model.locks.SpinLock;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.List;

/**
 * @author Gordon.Gan
 */
public class SyncThreadLocalNettyByteBufAllocator extends AbstractThreadLocalArrayAllocator<ByteBuf> {
    private SpinLock lock = new SpinLock();
    private volatile int count = 0;

    public SyncThreadLocalNettyByteBufAllocator(int maxCount, int bufferSize) {
        super(maxCount, bufferSize);
    }

    @Override
    public ByteBuf allocate() {
        lock.use(() -> {
            NettyByteBufAllocator.syncTotalCount.increment();
            if (count == 0) {
                NettyByteBufAllocator.syncAllocateCount.increment();
                return PooledByteBufAllocator.DEFAULT.directBuffer(bufferSize, bufferSize);
            } else {
                count -= 1;
                ByteBuf buffer = (ByteBuf) buffers[count];
                buffers[count] = null;
                return buffer;
            }
        });

        return null;
    }

    @Override
    public void release(ByteBuf array) {
        lock.use(() -> {
            if (count < maxCount) {
                array.clear();
                buffers[count++] = array;
            }
            return null;
        });
        NettyByteBufAllocator.syncDeallocateCount.increment();
        array.release();
    }

    @Override
    public void release(List<ByteBuf> arrays) {
        Integer index = arrays.size();
        index = Math.min(maxCount - count, index);
        int finalIndex = index;
        lock.use(() -> {
            for (int i = 0; i < finalIndex; i++) {
                arrays.get(i).clear();
                buffers[count++] = arrays.get(i);
            }
            return null;
        });
        for (int i = index; i < arrays.size(); i++) {
            NettyByteBufAllocator.syncDeallocateCount.increment();
            arrays.get(i).release();
        }
    }
}
