package com.gobrs.async.core.callback;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.common.domain.AsyncParam;
import com.gobrs.async.core.task.AsyncTask;

/**
 * The type Error com.gobrs.async.callback.
 *
 * @param <Param> the type parameter
 * @program: gobrs -async-starter
 * @ClassName ErrorCallback
 * @description: Exception com.gobrs.async.callback encapsulation
 * @author: sizegang
 * @create: 2022 -03-20
 */
public class ErrorCallback<Param> {

    /**
     * The Param.
     */
    AsyncParam<Param> param;
    /**
     * The Exception.
     */
    Exception exception;
    /**
     * The Support.
     */
    TaskSupport support;
    /**
     * The Task.
     */
    AsyncTask task;


    /**
     * Instantiates a new Error com.gobrs.async.callback.
     *
     * @param param     the param
     * @param exception the com.gobrs.async.exception
     * @param support   the support
     * @param task      the com.gobrs.async.com.gobrs.async.test.task
     */
    public ErrorCallback(AsyncParam param, Exception exception, TaskSupport support, AsyncTask task) {
        this.param = param;
        this.exception = exception;
        this.support = support;
        this.task = task;
    }

    /**
     * Gets param.
     *
     * @return the param
     */
    public AsyncParam<Param> getParam() {
        return param;
    }

    /**
     * Sets param.
     *
     * @param param the param
     */
    public void setParam(AsyncParam param) {
        this.param = param;
    }

    /**
     * Gets throwable.
     *
     * @return the throwable
     */
    public Exception getThrowable() {
        return exception;
    }

    /**
     * Sets throwable.
     *
     * @param throwable the throwable
     */
    public void setThrowable(Exception throwable) {
        this.exception = throwable;
    }

    /**
     * Gets support.
     *
     * @return the support
     */
    public TaskSupport getSupport() {
        return support;
    }

    /**
     * Sets support.
     *
     * @param support the support
     */
    public void setSupport(TaskSupport support) {
        this.support = support;
    }

    /**
     * Gets com.gobrs.async.com.gobrs.async.test.task.
     *
     * @return the com.gobrs.async.com.gobrs.async.test.task
     */
    public AsyncTask getTask() {
        return task;
    }

    /**
     * Sets com.gobrs.async.com.gobrs.async.test.task.
     *
     * @param task the com.gobrs.async.com.gobrs.async.test.task
     */
    public void setTask(AsyncTask task) {
        this.task = task;
    }




}
