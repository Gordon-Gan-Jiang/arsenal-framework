package com.arsenal.framework.model.io;
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
    private CharArrayAllocator allocator;

    public MultiCharArrayBuffer(boolean threadSafe) {
        super(threadSafe ? CharArrayAllocator.currentSync() : CharArrayAllocator.currentSimple());
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
        int bufferIndex = readPosition / allocator.bufferSize();
        int pos = readPosition - bufferIndex * allocator.bufferSize();
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
            int bufferIndex = start / allocator.bufferSize();
            int pos = start - bufferIndex * allocator.bufferSize();

            int countToRead = Math.min(end - start, allocator.bufferSize() - pos);
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

    private char[] newBuffer() {
        char[] buffer = allocator.allocate();
        buffers.add(buffer);
        position = 0;
        return buffer;
    }
}
