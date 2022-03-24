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
public abstract class AsyncTask<Param, Result> implements GobrsTask<Param, Result> {


    private String name;

    /**
     * Gets the execution results of dependencies
     *
     * @param support
     * @param clazz
     * @param resultClass
     * @param <R>
     * @return
     */
    public <R> R getResult(TaskSupport support, Class clazz, Class<R> resultClass) {
        Map<Class, TaskResult> resultMap = support.getResultMap();
        TaskResult<R> taskResult = resultMap.get(clazz) != null ? resultMap.get(clazz) : resultMap.get(depKey(clazz));
        if (taskResult != null) {
            return taskResult.getResult();
        }
        return null;
    }

    String depKey(Class clazz) {
        char[] cs = clazz.getSimpleName().toCharArray();
        cs[0] += 32;
        return String.valueOf(cs);
    }

    public boolean stopAsync(TaskSupport support) {
        try {
            ErrorCallback errorCallback = new ErrorCallback(() -> support.getParam(), null, support, this);
            support.taskLoader.setExpCode(new AtomicInteger(ExpState.DEFAULT.getCode()));
            support.taskLoader.errorInterrupted(errorCallback);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean stopAsync(TaskSupport support, Integer expCode) {
        try {
            ErrorCallback errorCallback = new ErrorCallback(() -> support.getParam(), null, support, this);
            support.taskLoader.setExpCode(new AtomicInteger(expCode));
            support.taskLoader.errorInterrupted(errorCallback);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
