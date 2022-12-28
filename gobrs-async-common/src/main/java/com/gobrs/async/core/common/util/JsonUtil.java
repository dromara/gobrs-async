package com.gobrs.async.core.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Json com.gobrs.async.util.
 *
 * @program: gobrs -async
 * @ClassName JsonUtil
 * @description:
 * @author: sizegang
 * @create: 2022 -07-05
 */
@Slf4j
public class JsonUtil {
    /**
     * The Logger.
     */

    private static ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Obj 2 string string.
     *
     * @param <T> the type parameter
     * @param obj the obj
     * @return the string
     */
    public static <T> String obj2String(T obj) {
        if(obj == null){
            return null;
        }
        String s = null;
        try {
            s = obj instanceof String ? (String)obj :  objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JsonUtil obj2String error", e);
        }
        return s;
    }

    /**
     * Obj 2 string pretty string.
     *
     * @param <T> the type parameter
     * @param obj the obj
     * @return the string
     */
    public static <T> String obj2StringPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * String 2 obj t.
     *
     * @param <T>   the type parameter
     * @param str   the str
     * @param clazz the clazz
     * @return the t
     */
    public static <T> T string2Obj(String str, Class<T> clazz) {
        if(str == null || str.length()==0 || clazz == null){
            return null;
        }
        T t = null;
        try {
            t = clazz.equals(String.class)? (T)str : objectMapper.readValue(str,clazz);
        } catch (IOException e) {
            log.error("JsonUtil string2Obj error{}", e);
        }
        return t;
    }

    /**
     * String 2 obj t.
     *
     * @param <T>           the type parameter
     * @param str           the str
     * @param typeReference the type reference
     * @return the t
     */
    public static <T> T string2Obj(String str, TypeReference<T> typeReference) {
        if (str ==null || str.length() ==0 || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str, typeReference));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * String 2 obj t.
     *
     * @param <T>             the type parameter
     * @param str             the str
     * @param collectionClazz the collection clazz
     * @param elementClazzes  the element clazzes
     * @return the t
     */
    public static <T> T string2Obj(String str, Class<?> collectionClazz, Class<?>... elementClazzes) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClazz, elementClazzes);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Builder json com.gobrs.async.util . json builder.
     *
     * @return the json com.gobrs.async.util . json builder
     */
    public static JsonUtil.JsonBuilder builder() {
        return new JsonUtil.JsonBuilder();
    }

    /**
     * The type Json builder.
     */
    public static class JsonBuilder {
        private Map<String ,Object> map = new HashMap<>();

        /**
         * Instantiates a new Json builder.
         */
        JsonBuilder() {
        }

        /**
         * Put json com.gobrs.async.util . json builder.
         *
         * @param key   the key
         * @param value the value
         * @return the json com.gobrs.async.util . json builder
         */
        public JsonUtil.JsonBuilder put(String key ,Object value){
            map.put(key,value);
            return this;
        }

        /**
         * Build string.
         *
         * @return the string
         */
        public String build() {

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.writeValueAsString(this.map);
            } catch (JsonProcessingException e) {
                log.error("JsonUtil build error{}", e);
            }
            return "{}";
        }
    }

}
