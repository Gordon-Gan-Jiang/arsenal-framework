package com.arsenal.framework.model.io;

import java.io.IOException;
import java.io.OutputStream;

public class MultiByteArrayOutputStream extends OutputStream {
    private final MultiByteArrayBuffer buffer;

    public MultiByteArrayOutputStream(MultiByteArrayBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int b) throws IOException {
        buffer.append((byte) b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        buffer.append(b, off, len);
    }

    public void release() {
        buffer.close();
    }
}

