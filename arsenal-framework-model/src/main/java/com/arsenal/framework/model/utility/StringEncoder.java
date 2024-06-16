package com.arsenal.framework.model.utility;

import static java.lang.Math.min;

import com.arsenal.framework.model.ArsenalConstant;
import com.arsenal.framework.model.io.MultiByteArrayBuffer;
import com.arsenal.framework.model.io.MultiByteBuffer;
import com.arsenal.framework.model.io.MultiCharArrayBuffer;
import com.arsenal.framework.model.io.MultiNettyByteBufBuffer;
import com.arsenal.framework.model.thread.FastThreadLocal;
import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;
import sun.nio.cs.ArrayDecoder;
import sun.nio.cs.ArrayEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;

/**
 * @author Gordon.Gan
 */
public class StringEncoder {

    private static final FastThreadLocal<CharsetEncoder> UTF8_ENCODE_THREAD_LOCAL = new FastThreadLocal<>();

    public static MultiByteArrayBuffer encodeToMultiByteArray(String string, boolean threadSafe, Charset charset)
            throws IOException {
        MultiByteArrayBuffer buffer = new MultiByteArrayBuffer(threadSafe);
        encodeStringInBatch(string, charset, buffer.asOutputStream());
        return buffer;
    }

    public static byte[] encodeToByteArray(String string, Charset charset) throws IOException {
        try (MultiByteArrayBuffer buffer = encodeToMultiByteArray(string, false, charset)) {
            return buffer.toByteArray();
        }
    }

    public static MultiByteArrayBuffer utf8EncodeToMultiByteArray(String string, boolean threadSafe)
            throws IOException {
        MultiByteArrayBuffer buffer = new MultiByteArrayBuffer(threadSafe);
        encodeStringToUtf8InBatch(string, buffer.asOutputStream());
        return buffer;
    }

    public static MultiByteBuffer utf8EncodeToMultiByteBuffer(String string, boolean threadSafe, boolean direct)
            throws IOException {
        MultiByteBuffer buffer = new MultiByteBuffer(threadSafe, direct);
        encodeStringToUtf8InBatch(string, buffer.asOutputStream());
        return buffer;
    }

    public static MultiNettyByteBufBuffer utf8EncodeToMultiNettyByteBuf(String string, boolean threadSafe)
            throws IOException {
        MultiNettyByteBufBuffer buffer = new MultiNettyByteBufBuffer(threadSafe);
        encodeStringToUtf8InBatch(string, buffer.asOutputStream());
        return buffer;
    }

    public static byte[] utf8EncodeToByteArray(String string) throws IOException {
        try (MultiByteArrayBuffer buffer = utf8EncodeToMultiByteArray(string, false)) {
            return buffer.toByteArray();
        }
    }

    public static void encodeStringInBatch(String string, Charset charset, OutputStream output) throws IOException {
        char[] charArray = getCharArrayFromString(string);
        encodeCharArrayInBatch(charArray, 0, charArray.length, charset, output);
    }

    public static void encodeStringToUtf8InBatch(String string, OutputStream output) throws IOException {
        char[] charArray = getCharArrayFromString(string);
        encodeCharArrayToUtf8InBatch(charArray, 0, charArray.length, output);
    }

    public static void encodeCharArrayInBatch(char[] chars, int offset, int len, Charset charset, OutputStream output)
            throws IOException {
        encodeCharArrayInBatch(chars, offset, len, charset.newEncoder(), output);
    }

    public static void encodeCharArrayToUtf8InBatch(char[] chars, int offset, int len, OutputStream output)
            throws IOException {
        CharsetEncoder encoder = UTF8_ENCODE_THREAD_LOCAL.safeGet(ArsenalConstant.UTF8_CHARSET::newEncoder);
        encoder.reset();
        encodeCharArrayInBatch(chars, offset, len, encoder, output);
    }

    public static void encodeCharArrayInBatch(char[] chars, int offset, int len, CharsetEncoder encoder, OutputStream output)
            throws IOException {
        byte[] bytes = IoHelper.getThreadByteArray();
        int batchSize = bytes.length / IoHelper.MAX_BYTES_PER_CHAR;

        if (encoder instanceof ArrayEncoder) {
            int start = offset;
            int end = offset + len;
            while (start < end) {
                int count = min(end - start, batchSize);
                int byteCount = ((ArrayEncoder) encoder).encode(chars, start, count, bytes);
                output.write(bytes, 0, byteCount);
                start += count;
            }
        } else {
            CharBuffer charBuffer = CharBuffer.wrap(chars, offset, len);
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            while (charBuffer.hasRemaining()) {
                if (charBuffer.remaining() < batchSize) {
                    encoder.encode(charBuffer, byteBuffer, true);
                } else {
                    encoder.encode(charBuffer, byteBuffer, false);
                }
                byteBuffer.flip();
                output.write(bytes, 0, byteBuffer.limit());
                byteBuffer.clear();
            }
        }
    }

    public static String readAsString(InputStream input, int length, Charset charset) throws IOException {
        byte[] byteArray = IoHelper.getThreadByteArray();
        if (length >= 0 && length <= byteArray.length) {
            return readSmallAsString(input, length, charset, byteArray);
        } else {
            return readBigAsString(input, charset);
        }
    }

    private static String readBigAsString(InputStream input, Charset charset) throws IOException {
        try (MultiCharArrayBuffer buffer = new MultiCharArrayBuffer(false)) {
            byte[] byteArray = IoHelper.getThreadByteArray();
            char[] charArray = IoHelper.getThreadCharArray();
            CharsetDecoder decoder = charset.newDecoder();
            ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
            CharBuffer charBuffer = CharBuffer.wrap(charArray);

            while (true) {
                int count = input.read(byteArray, byteBuffer.position(), byteBuffer.limit() - byteBuffer.position());
                if (count != -1) {
                    byteBuffer.position(byteBuffer.position() + count);
                    byteBuffer.flip();
                    decoder.decode(byteBuffer, charBuffer, false);
                    charBuffer.flip();
                    buffer.append(charArray, 0, charBuffer.limit());
                    charBuffer.clear();
                    byteBuffer.compact();
                } else {
                    byteBuffer.flip();
                    decoder.decode(byteBuffer, charBuffer, true);
                    decoder.flush(charBuffer);
                    charBuffer.flip();
                    buffer.append(charArray, 0, charBuffer.limit());
                    charBuffer.clear();
                    byteBuffer.compact();
                    break;
                }
            }
            return Arrays.toString(buffer.toCharArray());
        }
    }

    private static String readSmallAsString(InputStream input, int len, Charset charset, byte[] byteArray) throws IOException {
        char[] charArray = IoHelper.getThreadCharArray();
        Preconditions.checkArgument(byteArray.length == charArray.length);

        int count = IOUtils.read(input, byteArray, 0, len);
        Preconditions.checkState(count == len);
        CharsetDecoder decoder = charset.newDecoder();

        if (decoder instanceof ArrayDecoder) {
            int charCount = ((ArrayDecoder) decoder).decode(byteArray, 0, len, charArray);
            return new String(charArray, 0, charCount);
        } else {
            ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
            CharBuffer charBuffer = CharBuffer.wrap(charArray);
            byteBuffer.position(count);
            byteBuffer.flip();
            decoder.decode(byteBuffer, charBuffer, true);
            byteBuffer.clear();
            charBuffer.flip();
            return new String(charArray, 0, charBuffer.limit());
        }
    }

    private static char[] getCharArrayFromString(String string) {
        // Access the value field of the string using reflection
        try {
            java.lang.reflect.Field valueField = String.class.getDeclaredField("value");
            valueField.setAccessible(true);
            return (char[]) valueField.get(string);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
