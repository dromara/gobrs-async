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
     * Transaction task
     */
    private boolean callback = DefaultConfig.transaction;

    private int retryCount = DefaultConfig.retryCount;

    /**
     * Whether to execute a subtask if it fails
     */
    private boolean failSubExec = DefaultConfig.failSubExec;

    /**
     * if true => execute when any of parentTasks finished
     */

    private boolean any = false;

    /**
     * Whether any interruption ends other dependent tasks
     */
    private boolean exclusive = false;

    /**
     * get result of depend on class
     *
     * @param support
     * @param clazz   depend on class
     * @param <R>
     * @return
     */
    public <R> R getResult(TaskSupport support, Class<? extends Task> clazz) {
        TaskResult<R> taskResult = getTaskResult(support, clazz);
        if (taskResult != null) {
            return taskResult.getResult();
        }
        return null;
    }

    /**
     * get taskResult of depend on class
     *
     * @param support
     * @param clazz   depend on class
     * @param <R>     TaskResult<R>
     * @return
     */
    private <R> TaskResult<R> getTaskResult(TaskSupport support, Class<? extends Task> clazz) {
        Map<Class, TaskResult> resultMap = support.getResultMap();
        return resultMap.get(clazz) != null ? resultMap.get(clazz) : resultMap.get(depKey(clazz));
    }

    /**
     * get result of depend on class
     *
     * @param support
     * @param clazz   depend on class of AsyncTask
     * @param <R>     auto suggestion by IDE
     * @return
     */
    public <R> R getDependResult(TaskSupport support, Class<? extends AsyncTask<Param, R>> clazz) {
        TaskResult<R> taskResult = getTaskResult(support, clazz);
        if (taskResult != null) {
            return taskResult.getResult();
        }
        return null;
    }

    /**
     * get taskResult of depend on class
     *
     * @param support
     * @param clazz   depend on class of AsyncTask
     * @param <R>     auto suggestion by IDE
     * @return
     */
    public <R> TaskResult<R> getDependTaskResult(TaskSupport support, Class<? extends AsyncTask<Param, R>> clazz) {
        return getTaskResult(support, clazz);
    }

    /**
     * get result of current task
     *
     * @param support
     * @return
     */
    public Result getResult(TaskSupport support) {
        TaskResult<Result> taskResult = getTaskResult(support);
        if (taskResult != null) {
            return taskResult.getResult();
        }
        return null;
    }

    /**
     * get taskResult of current task
     *
     * @param support
     * @return
     */
    public TaskResult<Result> getTaskResult(TaskSupport support) {
        Map<Class, TaskResult> resultMap = support.getResultMap();
        Class thisResultClass = this.getClass();
        return resultMap.get(thisResultClass) != null ? resultMap.get(thisResultClass) : resultMap.get(depKey(thisResultClass));
    }

    /**
     * get task param
     *
     * @param support
     * @return
     */
    public Param getParam(TaskSupport support) {
        Object taskResult = support.getParam();
        if (taskResult != null) {
            return (Param) taskResult;
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
            logger.error("stopAsync error {}", ex);
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
            logger.error("stopAsync error {} ", ex);
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


    public boolean isAny() {
        return any;
    }

    public void setAny(boolean any) {
        this.any = any;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }
}
