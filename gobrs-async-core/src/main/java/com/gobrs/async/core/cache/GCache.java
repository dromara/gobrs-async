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
public interface GCache<K, V, I> {

    /**
     * Gets cache.
     *
     * @param k the k
     * @return the cache
     */
    V getCache(K k);

    /**
     * Sets cache.
     *
     * @param k the k
     * @param v the v
     */
    void setCache(K k, V v);

    /**
     * Gets type.
     *
     * @return the type
     */
    Integer getType();

    I instance();

}
