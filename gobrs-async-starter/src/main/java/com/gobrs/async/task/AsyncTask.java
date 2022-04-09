package com.gobrs.async.task;


import com.gobrs.async.TaskSupport;
import com.gobrs.async.callback.ErrorCallback;
import com.gobrs.async.def.DefaultConfig;
import com.gobrs.async.domain.TaskResult;
import com.gobrs.async.enums.ExpState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: gobrs-async-starter
 * @ClassName AsyncTask
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/
public abstract class AsyncTask<Param, Result> implements GobrsTask<Param, Result> {

    Logger logger = LoggerFactory.getLogger(AsyncTask.class);

    private String name;

    /**
     *Transaction task
     */
    private boolean callback = DefaultConfig.transaction;

    private int retryCount = DefaultConfig.retryCount;

    /**
     * Whether to execute a subtask if it fails
     */
    private boolean failSubExec = DefaultConfig.failSubExec;

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
            logger.error("stopAsync error ", ex);
            return false;
        }
        return true;
    }

    public boolean stopAsync(TaskSupport support, Integer expCode) {
        try {
            support.taskLoader.setIsRunning(false);
            support.taskLoader.setExpCode(new AtomicInteger(expCode));

            ErrorCallback errorCallback = new ErrorCallback(() -> support.getParam(), null, support, this);
            support.taskLoader.errorInterrupted(errorCallback);

        } catch (Exception ex) {
            logger.error("stopAsync error ", ex);
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

    public boolean isCallback() {
        return callback;
    }

    public void setCallback(boolean callback) {
        this.callback = callback;
    }


    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public boolean isFailSubExec() {
        return failSubExec;
    }

    public void setFailSubExec(boolean failSubExec) {
        this.failSubExec = failSubExec;
    }
}
