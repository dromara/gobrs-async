package com.jd.gobrs.async.task;

import com.jd.gobrs.async.callback.ICallback;
import com.jd.gobrs.async.callback.ITask;
import com.jd.gobrs.async.constant.GobrsAsyncConstant;
import com.jd.gobrs.async.constant.StateConstant;
import com.jd.gobrs.async.gobrs.GobrsFlowState;
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
public interface AsyncTask<T, V, P> extends ITask<T, V>, ICallback<T, V> {
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
    default P getData(Map<String, TaskWrapper> datasources, Long businessId, Class clazz) {
        TaskWrapper taskWrapper = datasources.get(clazz.getSimpleName()) != null
                ? datasources.get(clazz.getSimpleName()) : datasources.get(depKey(clazz));
        if (taskWrapper == null) {
            return null;
        }
        return (P) taskWrapper.getWorkResult(businessId);
    }

    /**
     * The caller closes the workflow
     * @param datasources
     * @param businessId
     * @param capCode
     * @return
     */
    default boolean stopTaskFlow(Map<String, TaskWrapper> datasources, Long businessId, Integer capCode) {
        Class<? extends AsyncTask> aClass = this.getClass();
        TaskWrapper taskWrapper = datasources.get(aClass.getSimpleName()) != null
                ? datasources.get(aClass.getSimpleName()) : datasources.get(depKey(aClass));
        if (taskWrapper == null) {
            return false;
        }

        boolean b = GobrsFlowState.compareAndSetState(StateConstant.WORKING, StateConstant.STOP, businessId);
        if (b) {
            GobrsFlowState.gobrsFlowState.get(businessId).setCapCode(capCode);
        }
        return b;
    }


    default void stopTaskFlow(Map<String, TaskWrapper> datasources, Long businessId) {
        stopTaskFlow(datasources, businessId, GobrsAsyncConstant.DEFAULT_CATCODE);
    }

}
