package com.gobrs.async.callback;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.domain.AsyncParam;
import com.gobrs.async.task.AsyncTask;

/**
 * @program: gobrs-async-starter
 * @ClassName ErrorCallback
 * @description: Exception callback encapsulation
 * @author: sizegang
 * @create: 2022-03-20
 **/
public class ErrorCallback<Param> {

    AsyncParam<Param> param;
    Exception exception;
    TaskSupport support;
    AsyncTask task;


    public ErrorCallback(AsyncParam param, Exception exception, TaskSupport support, AsyncTask task) {
        this.param = param;
        this.exception = exception;
        this.support = support;
        this.task = task;
    }

    public AsyncParam<Param> getParam() {
        return param;
    }

    public void setParam(AsyncParam param) {
        this.param = param;
    }

    public Exception getThrowable() {
        return exception;
    }

    public void setThrowable(Exception throwable) {
        this.exception = throwable;
    }

    public TaskSupport getSupport() {
        return support;
    }

    public void setSupport(TaskSupport support) {
        this.support = support;
    }

    public AsyncTask getTask() {
        return task;
    }

    public void setTask(AsyncTask task) {
        this.task = task;
    }




}
