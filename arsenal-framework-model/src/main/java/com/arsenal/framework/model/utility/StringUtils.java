package com.arsenal.framework.model.utility;

import com.arsenal.framework.model.io.MultiByteArrayBuffer;
import com.arsenal.framework.model.io.MultiNettyByteBufBuffer;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class StringUtils {
    private static final Utf8EncoderThreadLocal UTF8_ENCODE_THREAD_LOCAL = new Utf8EncoderThreadLocal();

    public static MultiByteArrayBuffer encodeToMultiByteArray(String input, boolean threadSafe, Charset charset) {
        MultiByteArrayBuffer buffer = new MultiByteArrayBuffer(threadSafe);
        encodeStringInBatch(input, charset, buffer.asOutputStream());
        return buffer;
    }

    public static byte[] encodeToByteArray(String input, Charset charset) {
        try (MultiByteArrayBuffer buffer = encodeToMultiByteArray(input, false, charset)) {
            return buffer.toByteArray();
        }
    }

    public static MultiByteArrayBuffer utf8EncodeToMultiByteArray(String input, boolean threadSafe) {
        MultiByteArrayBuffer buffer = new MultiByteArrayBuffer(threadSafe);
        encodeStringToUtf8InBatch(input, buffer.asOutputStream());
        return buffer;
    }

    public static MultiByteBuffer utf8EncodeToMultiByteBuffer(String input, boolean threadSafe, boolean direct) {
        MultiByteBuffer buffer = new MultiByteBuffer(threadSafe, direct);
        encodeStringToUtf8InBatch(input, buffer.asOutputStream());
        return buffer;
    }

    public static MultiNettyByteBufBuffer utf8EncodeToMultiNettyByteBuf(String input, boolean threadSafe) {
        MultiNettyByteBufBuffer buffer = new MultiNettyByteBufBuffer(threadSafe);
        encodeStringToUtf8InBatch(input, buffer.asOutputStream());
        return buffer;
    }

    public static byte[] utf8EncodeToByteArray(String input) {
        try (MultiByteArrayBuffer buffer = utf8EncodeToMultiByteArray(input, false)) {
            return buffer.toByteArray();
        }
    }

    /**
     * Encode string to byte array.
     */
    private static void encodeStringInBatch(String input, Charset charset, OutputStream output) {
        char[] charArray = input.toCharArray();
        encodeCharArrayInBatch(charArray, 0, charArray.length, charset, output);
    }

    /**
     * Encode string to byte array.
     */
    private static void encodeStringToUtf8InBatch(String input, OutputStream output) {
        char[] charArray = input.toCharArray();
        encodeCharArrayToUtf8InBatch(charArray, 0, charArray.length, output);
    }

    /**
     * Encode char array to byte array.
     */
    private static void encodeCharArrayInBatch(
            char[] chars, int offset, int len, Charset charset, OutputStream output) {
        CharsetEncoder encoder = UTF8_ENCODE_THREAD_LOCAL.get();
        encoder.reset();
        IoHelper.encodeCharArrayInBatch(chars, offset, len, encoder, output);
    }

    /**
     * Encode char array to byte array.
     */
    private static void encodeCharArrayToUtf8InBatch(
            char[] chars, int offset, int len, OutputStream output) {
        CharsetEncoder encoder = UTF8_ENCODE_THREAD_LOCAL.get();
        encoder.reset();
        IoHelper.encodeCharArrayInBatch(chars, offset, len, encoder, output);
    }
}

