package com.gobrs.async.core.common.domain;

/**
 * The interface Async param.
 *
 * @param <T> the type parameter
 * @program: gobrs -async-starter
 * @ClassName AsyncParam
 * @description:
 * @author: sizegang
 * @create: 2022 -03-20
 */
@FunctionalInterface
public interface AsyncParam<T> {
    /**
     * Get t.
     *
     * @return the t
     */
    T get();
}
