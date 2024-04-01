// Copyright 2022 Kingsoft Office Software Inc. All rights reserved.

package com.arsenal.framework.model.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author Gordon.Gan
 */
public class JsonConverter {

    public static <T> String serialize(T obj) {
        try {
            return JsonMapperFactory.defaultMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void serializeTo(T obj, OutputStream output) {
        try {
            JsonMapperFactory.defaultMapper.writeValue(output, obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void serializeTo(T obj, JavaType type, OutputStream output) {
        try {
            JsonMapperFactory.defaultMapper.writerFor(type).writeValue(output, obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serialize to byte array using UTF-8.
     */
    public static <T> byte[] serializeAsBytes(T obj) {
        try {
            return JsonMapperFactory.defaultMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            return JsonMapperFactory.defaultMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object deserialize(String json, Type type) {
        try {
            return JsonMapperFactory.defaultMapper.readValue(json, JsonMapperFactory.getType(type));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            return JsonMapperFactory.defaultMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object deserialize(byte[] json, Type type) {
        try {
            return JsonMapperFactory.defaultMapper.readValue(json, JsonMapperFactory.getType(type));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object deserialize(InputStream json, Type type) {
        try {
            return JsonMapperFactory.defaultMapper.readValue(json, JsonMapperFactory.getType(type));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> deserializeList(String json, Class<T> elementClazz) {
        try {
            return JsonMapperFactory.defaultMapper.readValue(json,
                    JsonMapperFactory.getListType(elementClazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> deserializeList(byte[] json, Class<T> elementClazz) {
        try {
            return JsonMapperFactory.defaultMapper.readValue(json,
                    JsonMapperFactory.getListType(elementClazz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static <T, E> Map<T, E> deserializeMap(String json, Class<T> key, Class<E> value) {
        try {
            return JsonMapperFactory.defaultMapper.readValue(json, JsonMapperFactory.getMapType(key, value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, E> Map<T, E> deserializeMap(byte[] json, Class<T> key, Class<E> value) {
        try {
            return JsonMapperFactory.defaultMapper.readValue(json, JsonMapperFactory.getMapType(key, value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, E> T deserializeGenerics(String json, Class<T> type, Class<E> parameterType) {
        final JavaType valueType = JsonMapperFactory.defaultMapper.getTypeFactory().constructParametricType(type,
                parameterType);
        try {
            return JsonMapperFactory.defaultMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

    public static <T, E> T deserializeGenerics(byte[] json, Class<T> type, Class<E> parameterType) {
        final JavaType valueType = JsonMapperFactory.defaultMapper.getTypeFactory().constructParametricType(type,
                parameterType);
        try {
            return JsonMapperFactory.defaultMapper.readValue(json, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode readTree(String json) {
        try {
            return JsonMapperFactory.defaultMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode readTree(byte[] json) {
        try {
            return JsonMapperFactory.defaultMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> JsonNode valueToTree(T obj) {
        return JsonMapperFactory.defaultMapper.valueToTree(obj);
    }

    public static <T> T treeToValue(JsonNode node, Class<T> clazz) {
        try {
            return JsonMapperFactory.defaultMapper.treeToValue(node, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
