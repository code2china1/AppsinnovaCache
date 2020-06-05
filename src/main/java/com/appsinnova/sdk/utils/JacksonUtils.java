package com.appsinnova.sdk.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class JacksonUtils {
    
    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String toJsonString(Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            log.warn("object cannot be serialized, message:{}", e.getLocalizedMessage());
            return null;
        }
    }

    public static <T> T toObject(String jsonStr, TypeReference<T> reference) {
        try {
            return null == jsonStr ? null : MAPPER.readValue(jsonStr, reference);
        } catch (Exception e) {
            log.warn("content cannot be deserialized:{}, message:{}", jsonStr, e.getLocalizedMessage());
            return null;
        }
    }
}
