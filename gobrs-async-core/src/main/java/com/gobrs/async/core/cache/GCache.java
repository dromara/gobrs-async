package com.gobrs.async.core.cache;

/**
 * The interface G cache.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @program: gobrs -async
 * @ClassName GCache
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
public interface GCache<K, V> {

    /**
     * Gets cache.
     *
     * @param v the v
     * @return the cache
     */
    V getCache(K v);

    /**
     * Sets cache.
     *
     * @param k the k
     * @param v the v
     */
    void setCache(K k, V v);

    Integer getType();


}
