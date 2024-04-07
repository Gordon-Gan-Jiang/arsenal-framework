package com.arsenal.framework.rpc.ahc.entity;

import com.arsenal.framework.model.io.MultiNettyByteBufBuffer;
import io.netty.buffer.ByteBuf;
import org.asynchttpclient.request.body.RandomAccessBody;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.List;

/**
 * @author Gordon.Gan
 */
public class MultiNettyByteBufBody implements RandomAccessBody {
    private MultiNettyByteBufBuffer buffer;
    private List<ByteBuffer> nioBuffers;

    public MultiNettyByteBufBody(MultiNettyByteBufBuffer buffer) {
        this.buffer = buffer;
        this.nioBuffers = buffer.toNioBuffers();
    }

    @Override
    public long getContentLength() {
        return buffer.getLength();
    }

    @Override
    public void close() {
    }

    @Override
    public long transferTo(WritableByteChannel target) throws IOException {
        long sum = 0L;
        for (int i = 0; i < nioBuffers.size(); i++) {
            ByteBuffer buffer = nioBuffers.get(i);
            int remaining = buffer.remaining();
            if (remaining > 0) {
                int writeCount = target.write(buffer);
                sum += writeCount;
                if (writeCount < remaining) {
                    return sum;
                }
            }
        }
        return sum;
    }

    @Override
    public BodyState transferTo(ByteBuf target) {
        for (int i = 0; i < nioBuffers.size(); i++) {
            ByteBuffer buffer = nioBuffers.get(i);
            int remaining = buffer.remaining();
            if (remaining > 0) {
                target.writeBytes(buffer);
                return (i == nioBuffers.size() - 1) ? BodyState.STOP : BodyState.CONTINUE;
            }
        }
        return BodyState.STOP;
    }

    public void reset() {
        nioBuffers = buffer.toNioBuffers();
    }

    public void release() {
        buffer.close();
    }
}