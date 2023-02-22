package com.gobrs.async.core.cache;

import com.gobrs.async.core.common.enums.GCacheEnum;
import com.gobrs.async.core.common.enums.TaskEnum;
import com.gobrs.async.core.task.MethodTaskAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Method task cache.
 *
 * @program: gobrs -async
 * @ClassName MethodTaskCache
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
public class MethodTaskCache extends BaseCache<String, MethodTaskAdapter, Map<String, MethodTaskAdapter>> {

    private Map<String, MethodTaskAdapter> METHOD_TASK_CACHE = new ConcurrentHashMap<>();


    @Override
    public MethodTaskAdapter getCache(String k) {
        return METHOD_TASK_CACHE.get(k);
    }

    @Override
    public void setCache(String s, MethodTaskAdapter o) {
        METHOD_TASK_CACHE.put(s, o);
    }

    @Override
    public Integer getType() {
        return TaskEnum.METHOD.getType();
    }

    @Override
    public Map<String, MethodTaskAdapter> instance() {
        return METHOD_TASK_CACHE;
    }
}
