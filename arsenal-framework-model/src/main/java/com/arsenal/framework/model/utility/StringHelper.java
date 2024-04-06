package com.arsenal.framework.model.utility;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gordon.Gan
 */
public class StringHelper {

    private static final int LAST_3_BYTE_UTF_CHAR_CODE_POINT = 65535;
    private static final String REPLACEMENT_CHAR = "�";

    private static final String VALUE_TRUE = "true";
    private static final String VALUE_FALSE = "false";

    private static final String VALUE_ON = "on";
    private static final String VALUE_OFF = "off";

    private static final String VALUE_YES = "yes";
    private static final String VALUE_NO = "no";

    private static final String VALUE_1 = "1";
    private static final String VALUE_0 = "0";

    public static final Field STRING_VALUE_FIELD = FieldUtils.getDeclaredField(String.class, "value", true);

    public static final List<String> NUMBER_STRINGS = new ArrayList<>();
    public static final List<String> ONE_DIGITAL_STRINGS = new ArrayList<>();
    public static final List<String> TWO_DIGITAL_STRINGS = new ArrayList<>();
    public static final List<String> THREE_DIGITAL_STRINGS = new ArrayList<>();

    static {
        for (int i = 0; i <= 999; i++) {
            NUMBER_STRINGS.add(Integer.toString(i));
        }

        for (int i = 0; i <= 9; i++) {
            ONE_DIGITAL_STRINGS.add(String.format("%d", i));
        }

        for (int i = 0; i <= 99; i++) {
            TWO_DIGITAL_STRINGS.add(String.format("%02d", i));
        }

        for (int i = 0; i <= 999; i++) {
            THREE_DIGITAL_STRINGS.add(String.format("%03d", i));
        }
    }

    public static String left(String str, int len) {
        return StringUtils.left(str, len);
    }

    public static String substringAfterLast(String str, String separator) {
        return StringUtils.substringAfterLast(str, separator);
    }

    public static boolean isTrue(String str) {
        return VALUE_TRUE.equalsIgnoreCase(str);
    }

    public static boolean isFalse(String str) {
        return VALUE_FALSE.equalsIgnoreCase(str);
    }

    public static boolean isLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNotNullOrEmpty(String str) {
        return StringUtils.isNotEmpty(str);
    }

    public static boolean isNotNullOrBlank(String str) {
        return StringUtils.isNotBlank(str);
    }

    public static List<String> splitToNotBlank(String str, String separatorChars) {
        if (StringUtils.isEmpty(str)) {
            return new ArrayList<>();
        } else {
            String[] splits = StringUtils.split(str, separatorChars);
            List<String> result = new ArrayList<>();
            for (String split : splits) {
                if (StringUtils.isNotBlank(split)) {
                    result.add(split);
                }
            }
            return result;
        }
    }

    public static String removeContinuousWhitespaces(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }

        StringBuilder builder = new StringBuilder();
        String[] lines = str.split("[\\r\\n]");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (StringUtils.isNotBlank(trimmedLine)) {
                builder.append(trimmedLine).append(" ");
            }
        }
        return builder.toString().trim();
    }

    public static String removeLineEnding(String str) {
        return StringUtils.remove(str, '\n');
    }

    public static String toValid3ByteUtf8String(String str) {
        int length = str.length();
        StringBuilder builder = new StringBuilder(length);
        int offset = 0;
        while (offset < length) {
            int codepoint = str.codePointAt(offset);
            if (codepoint > LAST_3_BYTE_UTF_CHAR_CODE_POINT) {
                builder.append(REPLACEMENT_CHAR);
            } else if (Character.isValidCodePoint(codepoint)) {
                builder.appendCodePoint(codepoint);
            } else {
                builder.append(REPLACEMENT_CHAR);
            }
            offset += Character.charCount(codepoint);
        }
        return builder.toString();
    }

    public static Boolean strictToBoolean(String str) {
        if (VALUE_TRUE.equalsIgnoreCase(str)) {
            return true;
        } else if (VALUE_FALSE.equalsIgnoreCase(str)) {
            return false;
        } else {
            return null;
        }
    }

    public Boolean looseToBoolean(String string) {
        if (string.equalsIgnoreCase(VALUE_TRUE)) {
            return true;
        } else if (string.equalsIgnoreCase(VALUE_FALSE)) {
            return false;
        } else if (string.equalsIgnoreCase(VALUE_ON)) {
            return true;
        } else if (string.equalsIgnoreCase(VALUE_OFF)) {
            return false;
        } else if (string.equalsIgnoreCase(VALUE_YES)) {
            return true;
        } else if (string.equalsIgnoreCase(VALUE_NO)) {
            return false;
        } else if (string.equalsIgnoreCase(VALUE_1)) {
            return true;
        } else if (string.equalsIgnoreCase(VALUE_0)) {
            return false;
        } else {
            return null;
        }
    }

    public String wrapAsString(char[] charArray) {
        String result = new String();
        try {
            Field stringValueField = String.class.getDeclaredField("value");
            stringValueField.setAccessible(true);
            stringValueField.set(result, charArray);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }
}
