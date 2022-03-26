package com.gobrs.async;

import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.domain.TaskResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @program: gobrs-async-starter
 * @ClassName TaskSupport
 * @description:
 * @author: sizegang
 * @create: 2022-03-20
 **/

public class TaskSupport {

    public TaskLoader taskLoader;

    public ExecutorService executorService;

    /**
     * The task parameters
     */
    private Object param;


    /**
     * Task result encapsulation
     */
    private Map<Class, TaskResult> resultMap = new HashMap();


    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    public Map<Class, TaskResult> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<Class, TaskResult> resultMap) {
        this.resultMap = resultMap;
    }

    public TaskLoader getTaskLoader() {
        return taskLoader;
    }

    public void setTaskLoader(TaskLoader taskLoader) {
        this.taskLoader = taskLoader;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
