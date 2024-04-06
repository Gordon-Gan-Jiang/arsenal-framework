package com.arsenal.framework.model.utility;

/**
 * @author Gordon.Gan
 */
import io.netty.buffer.ByteBuf;

public class Utf8Utils {
    public static final int MAX_BYTES_PER_VALUE = 6;

    public static final int ONE_BYTE_MAX_VALUE = 0x7F;
    public static final int TWO_BYTE_MAX_VALUE = 0x07FF;
    public static final int THREE_BYTE_MAX_VALUE = 0xFFFF;
    public static final int FOUR_BYTE_MAX_VALUE = 0x1FFFFF;
    public static final int FIVE_BYTE_MAX_VALUE = 0x3FFFFFF;
    public static final int SIX_BYTE_MAX_VALUE = 0x7FFFFFFF;

    public static void encode(int codepoint, ByteBuf buffer) {
        if (codepoint < 0 || codepoint > SIX_BYTE_MAX_VALUE) {
            throw new IllegalArgumentException("invalid UTF-8 code point");
        }
        if (codepoint <= ONE_BYTE_MAX_VALUE) {
            buffer.writeByte(codepoint);
        } else if (codepoint <= TWO_BYTE_MAX_VALUE) {
            buffer.writeByte(((codepoint >>> 6) & 0x1F) | 0xC0);
            buffer.writeByte((codepoint & 0x3F) | 0x80);
        } else if (codepoint <= THREE_BYTE_MAX_VALUE) {
            buffer.writeByte(((codepoint >>> 12) & 0x0F) | 0xE0);
            buffer.writeByte(((codepoint >>> 6) & 0x3F) | 0x80);
            buffer.writeByte((codepoint & 0x3F) | 0x80);
        } else if (codepoint <= FOUR_BYTE_MAX_VALUE) {
            buffer.writeByte(((codepoint >>> 18) & 0x07) | 0xF0);
            buffer.writeByte(((codepoint >>> 12) & 0x3F) | 0x80);
            buffer.writeByte(((codepoint >>> 6) & 0x3F) | 0x80);
            buffer.writeByte((codepoint & 0x3F) | 0x80);
        } else if (codepoint <= FIVE_BYTE_MAX_VALUE) {
            buffer.writeByte(((codepoint >>> 24) & 0x03) | 0xF8);
            buffer.writeByte(((codepoint >>> 18) & 0x3F) | 0x80);
            buffer.writeByte(((codepoint >>> 12) & 0x3F) | 0x80);
            buffer.writeByte(((codepoint >>> 6) & 0x3F) | 0x80);
            buffer.writeByte((codepoint & 0x3F) | 0x80);
        } else { // SIX_BYTE_MAX_VALUE
            buffer.writeByte(((codepoint >>> 30) & 0x01) | 0xFC);
            buffer.writeByte(((codepoint >>> 24) & 0x3F) | 0x80);
            buffer.writeByte(((codepoint >>> 18) & 0x3F) | 0x80);
            buffer.writeByte(((codepoint >>> 12) & 0x3F) | 0x80);
            buffer.writeByte(((codepoint >>> 6) & 0x3F) | 0x80);
            buffer.writeByte((codepoint & 0x3F) | 0x80);
        }
    }

    public static int decode(ByteBuf buffer) {
        int first = buffer.readByte();
        if ((first & 0x80) == 0x00) {
            return first;
        } else if ((first & 0xE0) == 0xC0) {
            int second = buffer.readByte();
            return ((first & 0x1F) << 6) | (second & 0x3F);
        } else if ((first & 0xF0) == 0xE0) {
            int second = buffer.readByte();
            int third = buffer.readByte();
            return ((first & 0x0F) << 12) | ((second & 0x3F) << 6) | (third & 0x3F);
        } else if ((first & 0xF8) == 0xF0) {
            int second = buffer.readByte();
            int third = buffer.readByte();
            int forth = buffer.readByte();
            return ((first & 0x07) << 18) | ((second & 0x3F) << 12) | ((third & 0x3F) << 6) | (forth & 0x3F);
        } else if ((first & 0xFC) == 0xF8) {
            int second = buffer.readByte();
            int third = buffer.readByte();
            int forth = buffer.readByte();
            int fifth = buffer.readByte();
            return ((first & 0x03) << 24) | ((second & 0x3F) << 18) | ((third & 0x3F) << 12) | ((forth & 0x3F) << 6) | (fifth & 0x3F);
        } else if ((first & 0xFE) == 0xFC) {
            int second = buffer.readByte();
            int third = buffer.readByte();
            int forth = buffer.readByte();
            int fifth = buffer.readByte();
            int sixth = buffer.readByte();
            return ((first & 0x01) << 30) | ((second & 0x3F) << 24) | ((third & 0x3F) << 18) | ((forth & 0x3F) << 12) | ((fifth & 0x3F) << 6) | (sixth & 0x3F);
        } else {
            throw new IllegalArgumentException("invalid UTF-8 encoding");
        }
    }

    public static boolean isValid(byte value) {
        int intValue = value & 0xFF;
        return (intValue & 0x80) == 0x00 ||
                (intValue & 0xE0) == 0xC0 ||
                (intValue & 0xF0) == 0xE0 ||
                (intValue & 0xF8) == 0xF0 ||
                (intValue & 0xFC) == 0xF8 ||
                (intValue & 0xFE) == 0xFC;
    }
}

