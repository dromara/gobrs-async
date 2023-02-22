package com.gobrs.async.core.common.domain;

import lombok.Builder;

import java.util.HashMap;

/**
 * The type Gobrs param support.
 *
 * @program: gobrs -async
 * @ClassName GobrsParamBuilder
 * @description:
 * @author: sizegang
 * @create: 2023 -02-22
 */
public class GobrsParamSupport extends HashMap<Class<?>, Object> {
    /**
     * Create gobrs param support.
     *
     * @return the gobrs param support
     */
    public static GobrsParamSupport create() {
        return new GobrsParamSupport();
    }

    @Override
    public Object put(Class<?> key, Object value) {
        return super.put(key, value);
    }


    /**
     * Put next gobrs param support.
     *
     * @param key   the key
     * @param value the value
     * @return the gobrs param support
     */
    public GobrsParamSupport putNext(Class<?> key, Object value) {
        super.put(key, value);
        return this;
    }
}
