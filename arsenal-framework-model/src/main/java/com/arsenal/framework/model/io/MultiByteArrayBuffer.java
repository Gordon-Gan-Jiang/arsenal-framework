package com.arsenal.framework.model.io;

import com.arsenal.framework.model.io.pool.ByteArrayAllocator;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MultiByteArrayBuffer extends BaseMultiArrayBuffer<byte[]> {
    public MultiByteArrayBuffer(boolean threadSafe) {
        super(threadSafe ? ByteArrayAllocator.getCurrentSync() : ByteArrayAllocator.getCurrentSimple());
    }

    public MultiByteArrayInputStream asInputStream() {
        return new MultiByteArrayInputStream(this);
    }

    public MultiByteArrayOutputStream asOutputStream() {
        return new MultiByteArrayOutputStream(this);
    }
    public void append(byte b) {
        if (position + 1 >= currentBuffer.length) {
            currentBuffer = newBuffer();
        }
        currentBuffer[position++] = b;
        length++;
    }

    public void append(byte[] b, int offset, int len) {
        int start = offset;
        int end = offset + len;
        while (start < end) {
            if (position == currentBuffer.length) {
                currentBuffer = newBuffer();
            }
            int bufferRemain = currentBuffer.length - position;
            int byteCountToWrite = Math.min(end - start, bufferRemain);
            System.arraycopy(b, start, currentBuffer, position, byteCountToWrite);
            position += byteCountToWrite;
            length += byteCountToWrite;
            start += byteCountToWrite;
        }
    }

    public void append(ByteBuffer src) {
        while (src.hasRemaining()) {
            if (position == currentBuffer.length) {
                currentBuffer = newBuffer();
            }
            int bufferRemain = currentBuffer.length - position;
            int byteCountToWrite = Math.min(src.remaining(), bufferRemain);
            src.get(currentBuffer, position, byteCountToWrite);
            position += byteCountToWrite;
            length += byteCountToWrite;
        }
    }

    public int read(int readPosition) {
        if (readPosition >= length) {
            return -1;
        }
        int bufferIndex = readPosition / allocator.getBufferSize();
        int pos = readPosition - bufferIndex * allocator.getBufferSize();
        return buffers.get(bufferIndex)[pos] & 0xFF;
    }

    public int read(int readPosition, byte[] b, int off, int len) {
        if (readPosition >= length) {
            return -1;
        }

        int start = readPosition;
        int end = Math.min(readPosition + len, length);
        int totalCount = 0;
        int writePos = off;
        while (start < end) {
            int bufferIndex = start / allocator.getBufferSize();
            int pos = start - bufferIndex * allocator.getBufferSize();

            int countToRead = Math.min(end - start, allocator.getBufferSize() - pos);

            System.arraycopy(currentBuffer[bufferIndex], pos, b, writePos, countToRead);
            start += countToRead;
            totalCount += countToRead;
            writePos += countToRead;
        }
        return totalCount;
    }

    public void writeTo(OutputStream output) throws IOException {
        for (int i = 0; i < buffers.size() - 1; i++) {
            output.write(buffers.get(i));
        }
        int remain = length - (buffers.size() - 1) * buffers.get(0).length;
        if (remain > 0) {
            output.write(buffers.get(buffers.size() - 1), 0, remain);
        }
    }

    public byte[] toByteArray() {
        byte[] bytes = new byte[length];
        read(0, bytes, 0, length);
        return bytes;
    }

    public List<ByteBuffer> getReadBuffers() {
        List<ByteBuffer> readBuffers = new ArrayList<>();
        for (int i = 0; i < buffers.size() - 1; i++) {
            readBuffers.add(ByteBuffer.wrap(buffers.get(i)));
        }
        int remain = length - (buffers.size() - 1) * buffers.get(0).length;
        if (remain > 0) {
            readBuffers.add(ByteBuffer.wrap(buffers.get(buffers.size() - 1), 0, remain));
        }
        return readBuffers;
    }
}

