package com.gobrs.async.task;


import com.gobrs.async.TaskSupport;
import com.gobrs.async.callback.ErrorCallback;
import com.gobrs.async.domain.TaskResult;
import com.gobrs.async.enums.ExpState;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: gobrs-async-starter
 * @ClassName EventHandler
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/
public interface AsyncTask<Param, Result> extends Task {
    /**
     * Before the mission begins
     *
     * @param param
     */
    void prepare(Param param);

    /**
     * Tasks to be performed
     *
     * @param param
     * @return
     */
    Result task(Param param, TaskSupport support);

    /**
     * Whether a task needs to be executed
     *
     * @param param
     * @return
     */
    boolean nessary(Param param, TaskSupport support);

    /**
     * Task Executed Successfully
     *
     * @param support
     */
    void onSuccess(TaskSupport support);

    /**
     * Task execution failure
     *
     * @param support
     */
    void onFail(TaskSupport support);

    /**
     * Gets the execution results of dependencies
     *
     * @param support
     * @param clazz
     * @param resultClass
     * @param <R>
     * @return
     */
    default <R> R getResult(TaskSupport support, Class clazz, Class<R> resultClass) {
        Map<Class, TaskResult> resultMap = support.getResultMap();
        TaskResult<R> taskResult = resultMap.get(clazz) != null ? resultMap.get(clazz) : resultMap.get(depKey(clazz));
        if (taskResult != null) {
            return taskResult.getResult();
        }
        return null;
    }

    default String depKey(Class clazz) {
        char[] cs = clazz.getSimpleName().toCharArray();
        cs[0] += 32;
        return String.valueOf(cs);
    }

    default boolean stop(TaskSupport support) {
        try {
            ErrorCallback errorCallback = new ErrorCallback(() -> support.getParam(), null, support, this);
            support.taskLoader.setExpCode(new AtomicInteger(ExpState.DEFAULT.getCode()));
            support.taskLoader.errorInterrupted(errorCallback);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    default boolean stop(TaskSupport support, Integer expCode) {
        try {
            ErrorCallback errorCallback = new ErrorCallback(() -> support.getParam(), null, support, this);
            support.taskLoader.setExpCode(new AtomicInteger(expCode));
            support.taskLoader.errorInterrupted(errorCallback);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }



}
