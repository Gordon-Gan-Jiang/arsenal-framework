package com.arsenal.framework.model.utility;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.commons.io.IOUtils.DEFAULT_BUFFER_SIZE;

public class IoHelper {
    private static final int MAX_SIZE = 16 * 1024;
    public static final int MAX_BYTES_PER_CHAR = 3;

    /**
     * Thread local ByteArrayOutputStream object to reuse...
     */
    private static final ThreadLocal<ByteArrayOutputStream> BYTE_ARRAY_OUTPUT_STREAM = ThreadLocal.withInitial(() -> new ByteArrayOutputStream());
    private static final ThreadLocal<char[]> CHAR_ARRAY_THREAD_LOCAL = ThreadLocal.withInitial(() -> new char[DEFAULT_BUFFER_SIZE]);
    private static final ThreadLocal<byte[]> BYTE_ARRAY_THREAD_LOCAL = ThreadLocal.withInitial(() -> new byte[DEFAULT_BUFFER_SIZE]);

    public static ByteArrayOutputStream getThreadByteArrayOutputStream() {
        ByteArrayOutputStream outputStream = BYTE_ARRAY_OUTPUT_STREAM.get();
        outputStream.reset();
        return outputStream;
    }

    public static void returnByteArrayOutputStream(ByteArrayOutputStream output) {
        if (output.size() > MAX_SIZE) {
            // If the content is too large, we discard the byte array output stream.
            BYTE_ARRAY_OUTPUT_STREAM.set(null);
        }
    }

    public static char[] getThreadCharArray() {
        return CHAR_ARRAY_THREAD_LOCAL.get();
    }

    public static byte[] getThreadByteArray() {
        return BYTE_ARRAY_THREAD_LOCAL.get();
    }
}



