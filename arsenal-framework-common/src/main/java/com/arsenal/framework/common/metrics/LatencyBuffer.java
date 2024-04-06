package com.arsenal.framework.common.metrics;

/**
 * @author Gordon.Gan
 */

import static com.arsenal.framework.model.utility.Utf8Utils.MAX_BYTES_PER_VALUE;
import static io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess.UNSAFE;

import com.arsenal.framework.model.utility.JodaUtils;
import com.arsenal.framework.model.utility.Utf8Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.List;

public class LatencyBuffer {
    private int capacity = MIN_CAPACITY;
    private volatile int min = Integer.MAX_VALUE;
    private volatile int max = Integer.MIN_VALUE;
    private volatile long lastUpdateTime = 0L;
    private volatile int count = 0;
    private ByteBuf latencies = PooledByteBufAllocator.DEFAULT.directBuffer(capacity);

    public LatencyBuffer(int capacity) {
        this.capacity = Math.max(capacity, MIN_CAPACITY);
    }

    public LatencyBuffer() {
    }

    public void writeTo(List<Integer> list) {
        while (latencies.isReadable()) {
            list.add(Utf8Utils.decode(latencies));
        }
        latencies.readerIndex(0);
    }

    public void add(int latency) {
        addLatency(latency);

        if (min > latency) {
            min = latency;
        }
        if (max < latency) {
            max = latency;
        }

        lastUpdateTime = System.currentTimeMillis();
        UNSAFE.storeFence();
    }

    public void release() {
        latencies.release();
    }

    public LatencyBuffer shrink() {
        int localCount = count;
        int localSize = latencies.writerIndex();
        long localLastUpdateTime = lastUpdateTime;
        int newCapacity = calculateNewCapacity(localSize);

        boolean cleanLegacy = System.currentTimeMillis() - localLastUpdateTime > LEGACY_DATA_EXPIRE_TIME;

        if (cleanLegacy) {
            return copy(0, 0, localCount, newCapacity);
        } else if (localSize < RETAIN_COUNT) {
            return copy(0, localSize, localCount, newCapacity);
        } else if (localSize < MIN_CAPACITY) {
            return copy(localSize - RETAIN_COUNT, localSize, localCount, newCapacity);
        } else {
            return copy(localSize - CAPACITY_STEP, localSize, localCount, newCapacity);
        }
    }

    private LatencyBuffer copy(int start, int end, int count, int newCapacity) {
        int processedCount = 0;
        while (latencies.readerIndex() < start && processedCount < count) {
            Utf8Utils.decode(latencies);
            processedCount++;
        }

        LatencyBuffer copiedBuffer = new LatencyBuffer(newCapacity);
        while (latencies.readerIndex() < end && processedCount < count) {
            int latency = Utf8Utils.decode(latencies);
            processedCount++;
            copiedBuffer.addLatency(latency);
        }

        latencies.readerIndex(0);
        copiedBuffer.lastUpdateTime = lastUpdateTime;
        return copiedBuffer;
    }

    private int calculateNewCapacity(int localSize) {
        if (localSize == capacity) {
            return Math.min(capacity + CAPACITY_STEP, MAX_CAPACITY);
        } else if (localSize <= (capacity - CAPACITY_STEP) / 2) {
            return Math.max(MIN_CAPACITY, capacity - CAPACITY_STEP);
        } else {
            return capacity;
        }
    }

    private void addLatency(int latency) {
        if (latencies.isWritable(MAX_BYTES_PER_VALUE)) {
            Utf8Utils.encode(latency, latencies);
            count++;
        }
    }

    private static final int MIN_CAPACITY = 512;
    private static final int MAX_CAPACITY = 4096;
    private static final int CAPACITY_STEP = 256;
    private static final int RETAIN_COUNT = 128;
    private static final long LEGACY_DATA_EXPIRE_TIME = JodaUtils.MILLISECONDS_PER_MINUTE * 20;
}
