package com.gobrs.async.core.holder;

import com.gobrs.async.core.cache.GCacheManager;
import com.gobrs.async.core.common.enums.TaskEnum;
import org.springframework.beans.BeanUtils;

/**
 * The type Bean proxy.
 *
 * @program: gobrs -async
 * @ClassName BeanProxy
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
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
        Object bean;
        try {
            bean = BeanHolder.getBean(name);
        } catch (Exception exception) {
            bean = gCacheManage.getGCache(type).getCache(name);
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
