package com.arsenal.framework.model.utility;

import com.arsenal.framework.model.json.JsonConverter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Gordon.Gan
 */
public class FieldUtils {
    public static <T extends Object> void setPropertyValue(Field property, T t, Object value) {
        property.setAccessible(true);
        try {
            property.set(t, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Object parseFromJson(Field property, String value) {
        return JsonConverter.deserialize(value, property.getType());
    }

    public static Object parseFromString(Field property, String value) {
        if (isTargetClassType(property, Boolean.class)) {
            return Boolean.valueOf(value);
        }

        if (isTargetClassType(property, Integer.class)) {
            return Integer.valueOf(value);
        }

        if (isTargetClassType(property, Long.class)) {
            return Long.valueOf(value);
        }

        if (isTargetClassType(property, BigDecimal.class)) {
            return new BigDecimal(value);
        }

        if (isTargetClassType(property, Pattern.class)) {
            return Pattern.compile(value);
        }

        if (isTargetClassType(property, Duration.class)) {
            return Duration.parse(value);
        }

        if (isTargetClassType(property, org.joda.time.Duration.class)) {
            return org.joda.time.Duration.parse(value);
        }

        if (isTargetClassType(property, List.class)) {
            final Type type = property.getType();
            ParameterizedType parameterizedType = (ParameterizedType) type;
            final Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
            if (actualTypeArgument.getTypeName().equals(Boolean.class.getTypeName())) {
                return Arrays.stream(value.split(";")).map(it -> Boolean.valueOf(it)).collect(Collectors.toList());
            }

            if (actualTypeArgument.getTypeName().equals(Integer.class.getTypeName())) {
                return Arrays.stream(value.split(";")).map(it -> Integer.valueOf(it)).collect(Collectors.toList());
            }

            if (actualTypeArgument.getTypeName().equals(Long.class.getTypeName())) {
                return Arrays.stream(value.split(";")).map(it -> Long.valueOf(it)).collect(Collectors.toList());
            }

            if (actualTypeArgument.getTypeName().equals(BigDecimal.class.getTypeName())) {
                return Arrays.stream(value.split(";")).map(it -> new BigDecimal(it)).collect(Collectors.toList());
            }

            if (actualTypeArgument.getTypeName().equals(Pattern.class.getTypeName())) {
                return Arrays.stream(value.split(";")).map(it -> Pattern.compile(it)).collect(Collectors.toList());
            }

            if (actualTypeArgument.getTypeName().equals(Duration.class.getTypeName())) {
                return Arrays.stream(value.split(";")).map(it -> Duration.parse(it)).collect(Collectors.toList());
            }

            if (actualTypeArgument.getTypeName().equals(org.joda.time.Duration.class.getTypeName())) {
                return Arrays.stream(value.split(";")).map(it -> org.joda.time.Duration.parse(it)).collect(
                        Collectors.toList());
            }
        }
        throw new RuntimeException("Property " + property.getName() + " cannot use type: " + property.getType());
    }

    public static boolean isTargetClassType(Field field, Class targetType) {
        return field.getType() == targetType;
    }

    public static <T extends Object> void parseAndSetFromJson(Field property, T config, String value) {
        setPropertyValue(property, config, parseFromJson(property, value));
    }

    public static <T extends Object> void parseAndSetFromString(Field property, T config, String value) {
        setPropertyValue(property, config, parseFromString(property, value));
    }
}
