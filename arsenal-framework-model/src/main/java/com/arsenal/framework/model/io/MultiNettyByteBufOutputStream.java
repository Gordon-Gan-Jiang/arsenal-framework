package com.arsenal.framework.model.io;

import java.io.OutputStream;

/**
 * @author Gordon.Gan
 */
public class MultiNettyByteBufOutputStream extends OutputStream {
    private MultiNettyByteBufBuffer buffer;

    public MultiNettyByteBufOutputStream(MultiNettyByteBufBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int b) {
        buffer.append(b);
    }

    @Override
    public void write(byte[] b, int offset, int len) {
        buffer.append(b, offset, len);
    }
}