package com.jd.gobrs.async.task;

import com.jd.gobrs.async.callback.ICallback;
import com.jd.gobrs.async.callback.ITask;
import com.jd.gobrs.async.constant.StateConstant;
import com.jd.gobrs.async.gobrs.GobrsAsyncSupport;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: gobrs-async-starter
 * @ClassName AsyncTask
 * @description:
 * @author: sizegang
 * @Version 1.0
 **/
public interface AsyncTask<T, V> extends ITask<T, V>, ICallback<T, V> {
    /**
     * 根据业务实现 判断是否需要执行当前task
     *
     * @param params
     * @return
     */
    @Override
    boolean nessary(T params, GobrsAsyncSupport support);


    default String depKey(Class clazz) {
        char[] cs = clazz.getSimpleName().toCharArray();
        cs[0] += 32;
        return String.valueOf(cs);
    }

    /**
     * @param support
     * @param businessId
     * @param clazz
     * @param resultClass
     * @param <R>
     * @return
     */
    default <R> R getResult(GobrsAsyncSupport support, Long businessId, Class clazz, Class<R> resultClass) {
        ConcurrentHashMap<String, TaskResult<R>> resultMap = support.getWorkResult();
        TaskResult<R> rTaskResult = resultMap.get(clazz.getSimpleName()) != null ? resultMap.get(clazz.getSimpleName()) : resultMap.get(depKey(clazz));
        if (rTaskResult != null) {
            return rTaskResult.getResult();
        }
        return null;
    }

    /**
     * @param gobrsAsyncSupport
     * @param capCode
     * @return
     */
    default boolean stopTaskFlow(GobrsAsyncSupport gobrsAsyncSupport, Integer capCode) {
        return gobrsAsyncSupport.getTaskFlowState().compareAndSet(StateConstant.WORKING, StateConstant.STOP);
    }

}
