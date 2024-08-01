package com.arsenal.framework.model.utility;

import com.arsenal.framework.model.io.MultiCharArrayBuffer;
import com.google.common.base.Preconditions;
import org.apache.commons.io.IOUtils;
import sun.nio.cs.ArrayDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class InputStreamUtils {
    public static String readAsString(InputStream input, int length, Charset charset) throws IOException {
        byte[] byteArray = IoHelper.getThreadByteArray();
        if (length >= 0 && length <= byteArray.length) {
            return readSmallAsString(input, length, charset, byteArray);
        } else {
            return readBigAsString(input, charset);
        }
    }

    private static String readBigAsString(InputStream input, Charset charset) throws IOException {
        MultiCharArrayBuffer buffer = new MultiCharArrayBuffer(false);
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
        return buffer.toCharArray().wrapAsString();
    }

    private static String readSmallAsString(InputStream input, int len, Charset charset, byte[] byteArray) throws IOException {
        char[] charArray = IoHelper.getThreadCharArray();
        Preconditions.checkArgument(byteArray.length == charArray.length);

        int count = IOUtils.read(input, byteArray, 0, len);
        Preconditions.checkState(count == len);
        CharsetDecoder decoder = charset.newDecoder();

        return (decoder instanceof ArrayDecoder) ? new String(charArray, 0, decoder.decode(byteArray, 0, len, charArray)) :
                decodeSmallBuffer(byteArray, charArray, count, decoder);
    }

    private static String decodeSmallBuffer(byte[] byteArray, char[] charArray, int count, CharsetDecoder decoder) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray, 0, count);
        CharBuffer charBuffer = CharBuffer.wrap(charArray);
        byteBuffer.flip();
        return decoder.decode(byteBuffer, charBuffer, true).toString();
    }
}
