package com.arsenal.framework.model.utility;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public  class Utf8EncoderThreadLocal extends ThreadLocal<CharsetEncoder> {
    @Override
    protected CharsetEncoder initialValue() {
        return Charset.forName("UTF-8").newEncoder();
    }
}
