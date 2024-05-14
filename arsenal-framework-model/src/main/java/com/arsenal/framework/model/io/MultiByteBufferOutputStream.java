package com.arsenal.framework.model.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Gordon.Gan
 */
public class MultiByteBufferOutputStream extends OutputStream {
    private final MultiByteBuffer buffer;

    public MultiByteBufferOutputStream(MultiByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int b) throws IOException {
        buffer.append((byte) b);
    }

    @Override
    public void write(byte[] b, int offset, int len) throws IOException {
        buffer.append(b, offset, len);
    }

    public void release() {
        buffer.close();
    }
}
