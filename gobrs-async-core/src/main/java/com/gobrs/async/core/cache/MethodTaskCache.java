package com.gobrs.async.core.cache;

import com.gobrs.async.core.common.enums.GCacheEnum;

/**
 * The type Method task cache.
 *
 * @program: gobrs -async
 * @ClassName MethodTaskCache
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
public class MethodTaskCache extends BaseCache<String, Object> {


    @Override
    public Object getCache(String v) {
        return null;
    }

    @Override
    public void setCache(String s, Object o) {

    }

    @Override
    public Integer getType() {
        return GCacheEnum.METHOD_TASK.getType();
    }
}
