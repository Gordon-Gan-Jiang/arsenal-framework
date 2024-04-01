package com.arsenal.framework.config.spring;

import com.arsenal.framework.config.annotations.ArsenalConfiguration;
import com.arsenal.framework.config.annotations.EnvConfig;
import com.arsenal.framework.config.annotations.EvnConfigs;
import com.arsenal.framework.config.annotations.UseJsonFormat;
import com.arsenal.framework.model.ArsenalConstant;
import com.arsenal.framework.model.config.ProfileType;
import com.arsenal.framework.model.utility.DesUtils;
import com.arsenal.framework.model.utility.FieldUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Arsenal configuration provider.
 * @author Gordon.Gan
 */
public class ArsenalConfigurationProvider<T extends Object> {
    private static String VALUE_METHOD_NAME = "value";
    private static Method VALUE_METHOD;
    private static Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]*)");
    private static String PLACEHOLDERS_PREFIX = "com.arsenal.config.placeholders.";

    static {
        try {
            VALUE_METHOD = EnvConfig.class.getMethod(VALUE_METHOD_NAME);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private ProfileType profile;
    private String region;
    private T config;
    private Class clazz;
    private Environment environment;

    public ArsenalConfigurationProvider(ProfileType profile, String region, T config, Class clazz,
            Environment environment) {
        this.profile = profile;
        this.region = region;
        this.config = config;
        this.clazz = clazz;
        this.environment = environment;
    }

    public T get() {
        // Set values from annotations.
        List<Field> missedProperties = setValuesFromAnnotation(profile, region);
        Map<String, Object> propertyOverrideMap = new HashMap();
        // Replace values from application.properties.
        extractValuesFromProperties(propertyOverrideMap);
        for (Field property : missedProperties) {
            if (!propertyOverrideMap.containsKey(property.getName())) {
                throw new RuntimeException(
                        clazz.getName() + "." + property.getName() + " is not configured for profile " + profile);
            }
        }
        if (!propertyOverrideMap.isEmpty()) {
            setPropertyOverrides(propertyOverrideMap);
        }
        return config;
    }

    private void setPropertyOverrides(Map<String, Object> propertyOverrideMap) {
        for (Field property : clazz.getDeclaredFields()) {
            final Object value = propertyOverrideMap.get(property.getName());
            if (value != null) {
                FieldUtils.setPropertyValue(property, config, value);
            }
        }
    }

    private List<Field> setValuesFromAnnotation(ProfileType profile, String region) {
        List<Field> missedProperties = new ArrayList<>();
        for (Field property : clazz.getDeclaredFields()) {
            if (property.getName().equals("jacocoData")) {
                continue;
            }
            checkProperty(property);
            // Choose annotation.
            EnvConfig annotation = getValidAnnotation(profile, region, property);
            if (annotation == null) {
                missedProperties.add(property);
            } else {
                // Get value from annotation.
                String value = getValue(annotation);
                // Process placeholders, only for DEV and UNITTEST.
                if (profile == ProfileType.DEV || profile == ProfileType.UNITTEST) {
                    value = replacePlaceHolders(value);
                }

                if (property.getDeclaredAnnotation(UseJsonFormat.class) != null) {
                    FieldUtils.parseAndSetFromJson(property, config, value);
                } else {
                    FieldUtils.parseAndSetFromString(property, config, value);
                }
            }
        }
        return missedProperties;
    }

    private String replacePlaceHolders(String input) {
        String value = input;
        while (true) {
            final Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
            if (!matcher.find()) {
                return value;
            }
            final String placeholderName = matcher.group(1);
            final String placeholderPropertyName = PLACEHOLDERS_PREFIX + placeholderName;
            final String placeholderValue = environment.getProperty(placeholderPropertyName);
            value = StringUtils.replaceOnce(value, placeholderName,
                    placeholderValue == null ? "" : placeholderValue);

        }
    }

    /**
     * JMX jolokia does not support generic types well.
     */
    private void checkProperty(Field property) {
        final Type genericType = property.getGenericType();
        if (genericType != null) {
            throw new RuntimeException(
                    clazz.getName() + "." + property.getName() + "is generics type, which is not allowed.");
        }
    }

    private EnvConfig getValidAnnotation(ProfileType profile, String region, Field property) {
        List<EnvConfig> annotations = Arrays.stream(property.getDeclaredAnnotationsByType(EnvConfig.class)).collect(
                Collectors.toList());
        if (annotations.isEmpty()) {
            annotations = Arrays.stream(property.getAnnotations()).filter(
                    it -> it instanceof EvnConfigs).flatMap(envConfigs -> Arrays
                    .stream(((EvnConfigs) envConfigs).value())).collect(Collectors.toList());
        }

        // Find by profile
        List<EnvConfig> validAnnotations = annotations.stream().filter(
                it -> ArrayUtils.contains(it.env(), profile)).collect(Collectors.toList());
        if (validAnnotations.isEmpty()) {
            return null;
        }

        // Find by region: find by exactly matched region, or use the default region config.
        final List<EnvConfig> filteredAnnotations = validAnnotations.stream()
                .filter(it -> it.region().equals(region)).collect(Collectors.toList());
        if (filteredAnnotations.isEmpty()) {
            validAnnotations = validAnnotations.stream().filter(it -> StringUtils.isEmpty(it.region())).collect(
                    Collectors.toList());
        }
        if (validAnnotations.size() != 1) {
            throw new RuntimeException(
                    clazz.getName() + "." + property.getName() + " has more than one configurations for " + "profile: "
                            + profile + ", " + "region: " + region);
        }
        return validAnnotations.get(0);
    }

    private String getValue(EnvConfig annotation) {
        String value = StringUtils.EMPTY;
        try {
            value = (String) VALUE_METHOD.invoke(annotation);
            if (annotation.encryptedV2()) {
                String desKey = environment.getProperty(ArsenalConstant.DES_KEY_PROPERTY);
                value = DesUtils.decrypt2(value, desKey);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return value;
    }

    private void extractValuesFromProperties(Map<String, Object> propertyOverrideMap) {
        Annotation annotation = clazz.getDeclaredAnnotation(ArsenalConfiguration.class);
        if (annotation == null) {
            return;
        }

        for (Field property : clazz.getDeclaredFields()) {
            // Get value from application.properties, use its value in prior.
            String propertyKey = annotation + "." + property.getName();
            final String value = environment.getProperty(propertyKey);
            if (value != null) {
                Object propertyValue = null;
                final UseJsonFormat declaredAnnotation = property.getDeclaredAnnotation(UseJsonFormat.class);
                if (declaredAnnotation != null) {
                    propertyValue = FieldUtils.parseFromJson(property, value);
                } else {
                    propertyValue = FieldUtils.parseFromString(property, value);
                }
                propertyOverrideMap.put(property.getName(), propertyValue);
            }
        }
    }
}
