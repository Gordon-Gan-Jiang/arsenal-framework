package com.arsenal.framework.model.io;
import com.arsenal.framework.model.io.pool.CharArrayAllocator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gordon.Gan
 */

public class MultiCharArrayBuffer extends BaseMultiArrayBuffer<char[]> {
    private boolean threadSafe;
    private int position;
    private int length;
    private char[] currentBuffer;
    private List<char[]> buffers;

    public MultiCharArrayBuffer(boolean threadSafe) {
        super(threadSafe ? CharArrayAllocator.getCurrentSync() : CharArrayAllocator.getCurrentSimple());
        this.threadSafe = threadSafe;
        this.position = 0;
        this.length = 0;
        this.buffers = new ArrayList<>();
        this.currentBuffer = newBuffer();
    }

    public void append(int b) {
        if (position + 1 >= currentBuffer.length) {
            currentBuffer = newBuffer();
        }
        currentBuffer[position++] = (char) b;
        length++;
    }

    public void append(char[] b, int offset, int len) {
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

    public int read(int readPosition) {
        if (readPosition >= length) {
            return -1;
        }
        int bufferIndex = readPosition / allocator.getBufferSize();
        int pos = readPosition - bufferIndex * allocator.getBufferSize();
        return buffers.get(bufferIndex)[pos];
    }

    public int read(int readPosition, char[] b, int off, int len) {
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
            System.arraycopy(buffers.get(bufferIndex), pos, b, writePos, countToRead);
            start += countToRead;
            totalCount += countToRead;
            writePos += countToRead;
        }
        return totalCount;
    }

    public char[] toCharArray() {
        char[] chars = new char[length];
        read(0, chars, 0, length);
        return chars;
    }

    public char[] newBuffer() {
        char[] buffer = allocator.allocate();
        buffers.add(buffer);
        position = 0;
        return buffer;
    }
}
