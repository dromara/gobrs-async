package com.jd.gobrs.async.task;

import com.jd.gobrs.async.callback.ICallback;
import com.jd.gobrs.async.callback.ITask;
import com.jd.gobrs.async.wrapper.TaskWrapper;

import java.util.Map;

/**
 * @program: gobrs-async-starter
 * @ClassName AsyncTask
 * @description:
 * @author: sizegang
 * @create: 2022-01-28 00:16
 * @Version 1.0
 **/
public interface AsyncTask<T, V> extends ITask<T, V>, ICallback<T, V> {
    /**
     * 根据业务实现 判断是否需要执行当前task
     *
     * @param t
     * @return
     */
    @Override
    boolean nessary(T t);


    default String depKey(Class clazz) {
        char[] cs = clazz.getSimpleName().toCharArray();
        cs[0] += 32;
        return String.valueOf(cs);
    }

    /**
     * 获取依赖数据
     *
     * @param datasources 数据源中
     * @param clazz       源类
     * @param businessId  业务id
     * @return
     */
    default Object getData(Map<String, TaskWrapper> datasources, Long businessId, Class clazz) {
        TaskWrapper taskWrapper;
        if (datasources.get(clazz.getSimpleName()) != null) {
            taskWrapper = datasources.get(clazz.getSimpleName());
        } else {
            taskWrapper = datasources.get(depKey(clazz));
        }
        if (taskWrapper == null) {
            return null;
        }
        return taskWrapper.getWorkResult(businessId);
    }
}
