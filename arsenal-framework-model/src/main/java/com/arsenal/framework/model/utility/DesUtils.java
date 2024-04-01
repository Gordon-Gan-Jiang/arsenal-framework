package com.arsenal.framework.model.utility;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * @author Gordon.Gan
 */
public class DesUtils {

    private static final String KEY_ALGORITHM = "DES";
    private static final int SALT_SIZE = 32;
    private static final SecretKeyFactory KEY_FACTORY;

    static {
        try {
            KEY_FACTORY = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static final String V2 = "2.0";

    @Deprecated
    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        DESKeySpec desKey = new DESKeySpec(key);
        Cipher instance = Cipher.getInstance("DES/ECB/PKCS5PADDING");
        instance.init(Cipher.ENCRYPT_MODE, KEY_FACTORY.generateSecret(desKey), new SecureRandom());
        return instance.doFinal(addSalt(src));
    }

    @Deprecated
    public static String encrypt(String src, String key) throws Exception {
        return Base64.encodeBase64String(encrypt(src.getBytes("UTF-8"), Hex.decodeHex(key)));
    }

    public static byte[] encrypt2(byte[] src, byte[] key) throws Exception {
        DESKeySpec desKey = new DESKeySpec(key);
        Cipher instance = Cipher.getInstance("DES/ECB/PKCS5PADDING");
        instance.init(Cipher.ENCRYPT_MODE, KEY_FACTORY.generateSecret(desKey), new SecureRandom());
        return instance.doFinal(addSalt(src));
    }

    public static String encrypt2(String src, String key) throws Exception {
        return Base64.encodeBase64String(encrypt2(composeJson(src, key).getBytes("UTF-8"), Hex.decodeHex(key)));
    }

    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        DESKeySpec desKey = new DESKeySpec(key);
        Cipher instance = Cipher.getInstance(KEY_ALGORITHM);
        instance.init(Cipher.DECRYPT_MODE, KEY_FACTORY.generateSecret(desKey), new SecureRandom());
        return removeSalt(instance.doFinal(src));
    }

    public static String decrypt(String src, String key) throws Exception {
        return new String(decrypt(Base64.decodeBase64(src), Hex.decodeHex(key)), "UTF-8");
    }

    public static byte[] decrypt2(byte[] src, byte[] key) throws Exception {
        DESKeySpec desKey = new DESKeySpec(key);
        Cipher instance = Cipher.getInstance(KEY_ALGORITHM);
        instance.init(Cipher.DECRYPT_MODE, KEY_FACTORY.generateSecret(desKey), new SecureRandom());
        return removeSalt(instance.doFinal(src));
    }

    public static String decrypt2(String src, String key) throws Exception {
        byte[] decryptedData = decrypt2(Base64.decodeBase64(src), Hex.decodeHex(key));
        String decryptedJson = new String(decryptedData, "UTF-8");
        JsonData jsonData = JsonData.fromJson(decryptedJson);
        if (!jsonData.getKeyMd5().equals(Md5Utils.encode(key))) {
            throw new RuntimeException("Wrong key for decryption.");
        }
        return jsonData.getData();
    }

    private static byte[] addSalt(byte[] src) {
        SecureRandom random = new SecureRandom();
        byte[] part1 = new byte[SALT_SIZE / 2];
        byte[] part2 = new byte[SALT_SIZE / 2];
        random.nextBytes(part1);
        random.nextBytes(part2);
        byte[] result = new byte[SALT_SIZE + src.length];
        System.arraycopy(part1, 0, result, 0, part1.length);
        System.arraycopy(src, 0, result, part1.length, src.length);
        System.arraycopy(part2, 0, result, part1.length + src.length, part2.length);
        return result;
    }

    private static byte[] removeSalt(byte[] src) {
        if (src.length <= SALT_SIZE) {
            throw new IllegalArgumentException("Invalid encrypted text.");
        }
        byte[] result = new byte[src.length - SALT_SIZE];
        System.arraycopy(src, SALT_SIZE / 2, result, 0, result.length);
        return result;
    }

    private static String composeJson(String src, String key) {
        return new JsonData(V2, Md5Utils.encode(key), DateTime.now().toString(), src).toJson();
    }

    private static class JsonData {
        private final String version;
        private final String keyMd5;
        private final String time;
        private final String data;

        public JsonData(String version, String keyMd5, String time, String data) {
            this.version = version;
            this.keyMd5 = keyMd5;
            this.time = time;
            this.data = data;
        }

        public String getVersion() {
            return version;
        }

        public String getKeyMd5() {
            return keyMd5;
        }

        public String getTime() {
            return time;
        }

        public String getData() {
            return data;
        }

        public static JsonData fromJson(String json) {
            // Implement JSON parsing logic here
            // Return an instance of JsonData
            return null;
        }

        public String toJson() {
            // Implement JSON serialization logic here
            // Return the JSON string representation
            return null;
        }
    }
}

