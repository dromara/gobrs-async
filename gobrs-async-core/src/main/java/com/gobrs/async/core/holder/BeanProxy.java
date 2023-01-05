package com.gobrs.async.core.holder;

import com.gobrs.async.core.cache.GCacheManager;
import com.gobrs.async.core.common.enums.TaskEnum;
import com.gobrs.async.core.common.exception.AsyncTaskNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * The type Bean proxy.
 *
 * @program: gobrs -async
 * @ClassName BeanProxy
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
@Slf4j
public class BeanProxy {

    private static GCacheManager gCacheManage = BeanHolder.getBean(GCacheManager.class);

    /**
     * Gets bean.
     *
     * @param name the name
     * @param type the type
     * @return the bean
     */
    public static Object getBean(String name, Integer type) {
        Object bean = null;
        try {
            bean = BeanHolder.getBean(name);
        } catch (Exception exception) {
            try {
                bean = gCacheManage.getGCache(type).getCache(name);
            } catch (Exception e) {
            }
        } finally {
            if (bean == null) {
                throw new AsyncTaskNotFoundException(String.format("task %s %s", name, "Please check whether the configuration is correct."));
            }
        }
        return bean;
    }

    /**
     * Gets bean.
     *
     * @param name the name
     * @return the bean
     */
    public static Object getBean(String name) {
        return getBean(name, TaskEnum.METHOD.getType());
    }

}
