// Copyright 2022 Kingsoft Office Software Inc. All rights reserved.

package com.arsenal.framework.model.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * @author Gordon.Gan
 */
public class JsonMapperFactory {
    public static ObjectMapper defaultMapper = new ObjectMapper();
    public static ObjectMapper restrictMapper = new ObjectMapper();

    static {
        defaultMapper.setVisibility(
                defaultMapper.getVisibilityChecker()
                        .withFieldVisibility(Visibility.ANY)
                        .withGetterVisibility(Visibility.NONE)
                        .withIsGetterVisibility(Visibility.NONE));

        defaultMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS)
                // Fix jackson vs @ConstructorProperties issue: cannot deserialize json properly,
                // default values will be overwritten.
                .disable(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES);
        defaultMapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));

        // Serialize/deserialize Joda DateTime.

        defaultMapper.registerModule(new JodaModule() {
            {
                addSerializer(DateTime.class, new CstDateTimeSerializer(DateTime.class));
                addDeserializer(DateTime.class, new CstDateTimeDeserializer(DateTime.class));
            }
        })
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());

        restrictMapper.setVisibility(
                defaultMapper.getVisibilityChecker()
                        .withFieldVisibility(Visibility.NONE)
                        .withGetterVisibility(Visibility.NONE)
                        .withIsGetterVisibility(Visibility.NONE)
        );

        restrictMapper
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS)
                // Fix jackson vs @ConstructorProperties issue: cannot deserialize json properly,
                // default values will be overwritten.
                .disable(MapperFeature.INFER_CREATOR_FROM_CONSTRUCTOR_PROPERTIES);
        restrictMapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));


        // Serialize/deserialize Joda DateTime.
        restrictMapper
                .registerModule(new JodaModule() {
                    {
                        addSerializer(DateTime.class, new CstDateTimeSerializer(DateTime.class));
                        addDeserializer(DateTime.class, new CstDateTimeDeserializer(DateTime.class));
                    }
                })
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }


    public static CollectionType getListType(Class<?> clazz) {
        return defaultMapper.getTypeFactory().constructCollectionType(List.class, clazz);
    }


    public static MapType getMapType(Class<?> keyClass, Class<?> valueClass) {
        return defaultMapper.getTypeFactory().constructMapType(HashMap.class, keyClass, valueClass);

    }

    public static JavaType getType(Type type) {
        return defaultMapper.getTypeFactory().constructType(type);

    }
}
