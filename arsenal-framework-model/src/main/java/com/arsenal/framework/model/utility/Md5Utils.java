package com.arsenal.framework.model.utility;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;

/**
 * @author Gordon.Gan
 */
public class Md5Utils {
    private static final MessageDigest MD5;

    static {
        try {
            MD5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encode(String origin) {
        return Hex.encodeHexString(MD5.digest(origin.getBytes()));
    }
}
