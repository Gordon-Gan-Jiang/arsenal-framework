package com.arsenal.framework.model.io;

import java.io.IOException;
import java.io.InputStream;

public class MultiByteArrayInputStream extends InputStream {
    private final MultiByteArrayBuffer buffer;
    private int readPosition = 0;

    public MultiByteArrayInputStream(MultiByteArrayBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int available() {
        return buffer.getLength() - readPosition;
    }

    @Override
    public int read() throws IOException {
        return buffer.read(readPosition++);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int readCount = buffer.read(readPosition, b, off, len);
        if (readCount > 0) {
            readPosition += readCount;
        }
        return readCount;
    }

    @Override
    public synchronized void reset() throws IOException {
        readPosition = 0;
    }

    public void release() {
        buffer.close();
    }
}

