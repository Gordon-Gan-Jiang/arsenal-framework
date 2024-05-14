package com.arsenal.framework.model.io;

import com.arsenal.framework.model.io.pool.ByteBufferAllocator;
import com.arsenal.framework.model.utility.IoHelper;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MultiByteBuffer extends BaseMultiArrayBuffer<ByteBuffer> {
    private final boolean direct;

    public MultiByteBuffer(boolean threadSafe, boolean direct) {
        super(threadSafe ? (direct ? ByteBufferAllocator.getCurrentDirectSync() : ByteBufferAllocator.getCurrentHeapSync()) :
                (direct ? ByteBufferAllocator.getCurrentDirectSimple() : ByteBufferAllocator.getCurrentHeapSimple()));
        this.direct = direct;
    }

    public void append(byte b) {
        if (!currentBuffer.hasRemaining()) {
            currentBuffer = newBuffer();
        }
        currentBuffer.put(b);
        position++;
        length++;
    }

    public void append(byte[] b, int offset, int len) {
        int start = offset;
        int end = offset + len;
        while (start < end) {
            if (!currentBuffer.hasRemaining()) {
                currentBuffer = newBuffer();
            }
            int byteCountToWrite = Math.min(end - start, currentBuffer.remaining());
            currentBuffer.put(b, start, byteCountToWrite);
            position += byteCountToWrite;
            length += byteCountToWrite;
            start += byteCountToWrite;
        }
    }

    public void writeTo(OutputStream output) throws Exception {
        if (direct) {
            MultiByteBufferInputStream input = asInputStream();
            byte[] array = IoHelper.getThreadByteArray();
            int count;
            while ((count = input.read(array, 0, array.length)) >= 0) {
                output.write(array, 0, count);
            }
        } else {
            for (ByteBuffer buffer : getReadBuffers()) {
                byte[] array = new byte[buffer.remaining()];
                buffer.get(array);
                output.write(array);
            }
        }
    }

    public MultiByteBufferInputStream asInputStream() {
        return new MultiByteBufferInputStream(this);
    }

    public MultiByteBufferOutputStream asOutputStream() {
        return new MultiByteBufferOutputStream(this);
    }

    public List<ByteBuffer> getReadBuffers() {
        List<ByteBuffer> readBuffers = new ArrayList<>();
        for (ByteBuffer buffer : buffers) {
            readBuffers.add((ByteBuffer) buffer.duplicate().flip());
        }
        return readBuffers;
    }
}

