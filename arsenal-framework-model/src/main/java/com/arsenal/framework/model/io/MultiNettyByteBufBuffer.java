package com.arsenal.framework.model.io;


import com.arsenal.framework.model.io.pool.NettyByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gordon.Gan
 */
public class MultiNettyByteBufBuffer extends BaseMultiArrayBuffer<ByteBuf> {
    public MultiNettyByteBufBuffer(boolean threadSafe) {
        super(threadSafe ? NettyByteBufAllocator.getCurrentSimple() : NettyByteBufAllocator.getCurrentSync());
    }

    public void append(int b) {
        if (!currentBuffer.isWritable()) {
            currentBuffer = newBuffer();
        }
        currentBuffer.writeByte(b);
        position++;
        length++;
    }

    public void append(byte[] b, int offset, int len) {
        int start = offset;
        int end = offset + len;
        while (start < end) {
            if (!currentBuffer.isWritable()) {
                currentBuffer = newBuffer();

            }
            int bufferRemain = currentBuffer.writableBytes();
            int byteCountToWrite = Math.min(end - start, bufferRemain);
            currentBuffer.writeBytes(b, start, byteCountToWrite);
            position += byteCountToWrite;
            length += byteCountToWrite;
            start += byteCountToWrite;
        }
    }

    public MultiNettyByteBufOutputStream asOutputStream() {
        return new MultiNettyByteBufOutputStream(this);
    }

    public List<ByteBuffer> toNioBuffers() {
        List<ByteBuffer> nioBuffers = new ArrayList<>();
        for (ByteBuf buffer : buffers) {
            nioBuffers.add(buffer.nioBuffer());
        }
        return nioBuffers;
    }
}
