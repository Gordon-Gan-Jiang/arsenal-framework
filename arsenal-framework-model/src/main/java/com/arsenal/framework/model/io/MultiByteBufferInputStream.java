package com.arsenal.framework.model.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Gordon.Gan
 */
public class MultiByteBufferInputStream extends InputStream {
    private final MultiByteBuffer buffer;
    private List<ByteBuffer> byteBuffers;
    private ByteBuffer currentBuffer;
    private int bufferIndex;
    private int readPosition;

    public MultiByteBufferInputStream(MultiByteBuffer buffer) {
        this.buffer = buffer;
        this.byteBuffers = buffer.getReadBuffers();
        this.currentBuffer = byteBuffers.get(0);
        this.bufferIndex = 0;
        this.readPosition = 0;
    }

    @Override
    public int available() throws IOException {
        return buffer.getLength() - readPosition;
    }

    @Override
    public int read() throws IOException {
        if (readPosition >= buffer.getLength()) {
            return -1;
        }
        if (!currentBuffer.hasRemaining()) {
            nextBuffer();
        }
        readPosition++;
        return currentBuffer.get() & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (readPosition >= buffer.getLength()) {
            return -1;
        }

        int start = off;
        int end = Math.min(off + len, available());
        int totalCount = 0;
        while (start < end) {
            if (!currentBuffer.hasRemaining()) {
                nextBuffer();
            }
            int countToRead = Math.min(end - start, currentBuffer.remaining());
            currentBuffer.get(b, start, countToRead);
            start += countToRead;
            totalCount += countToRead;
        }
        readPosition += totalCount;
        return totalCount;
    }

    @Override
    public synchronized void reset() throws IOException {
        byteBuffers = buffer.getReadBuffers();
        currentBuffer = byteBuffers.get(0);
        bufferIndex = 0;
        readPosition = 0;
    }

    public void release() {
        buffer.close();
    }

    private void nextBuffer() {
        bufferIndex++;
        if (bufferIndex < byteBuffers.size()) {
            currentBuffer = byteBuffers.get(bufferIndex);
        } else {
            throw new RuntimeException("Buffer is empty!");
        }
    }
}

