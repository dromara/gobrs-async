package com.gobrs.async;

import com.gobrs.async.domain.TaskResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * The type Task support.
 *
 * @program: gobrs -async-starter
 * @ClassName TaskSupport
 * @description:
 * @author: sizegang
 * @create: 2022 -03-20
 */
public class TaskSupport {

    /**
     * The Task loader.
     */
    public TaskLoader taskLoader;

    /**
     * The Executor service.
     */
    public ExecutorService executorService;

    /**
     * The task parameters
     */
    private Object param;


    /**
     * Task result encapsulation
     */
    private Map<Class, TaskResult> resultMap = new HashMap();


    /**
     * Gets param.
     *
     * @return the param
     */
    public Object getParam() {
        return param;
    }

    /**
     * Sets param.
     *
     * @param param the param
     */
    public void setParam(Object param) {
        this.param = param;
    }

    /**
     * Gets result map.
     *
     * @return the result map
     */
    public Map<Class, TaskResult> getResultMap() {
        return resultMap;
    }

    /**
     * Sets result map.
     *
     * @param resultMap the result map
     */
    public void setResultMap(Map<Class, TaskResult> resultMap) {
        this.resultMap = resultMap;
    }

    /**
     * Gets task loader.
     *
     * @return the task loader
     */
    public TaskLoader getTaskLoader() {
        return taskLoader;
    }

    /**
     * Sets task loader.
     *
     * @param taskLoader the task loader
     */
    public void setTaskLoader(TaskLoader taskLoader) {
        this.taskLoader = taskLoader;
    }

    /**
     * Gets executor service.
     *
     * @return the executor service
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Sets executor service.
     *
     * @param executorService the executor service
     */
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
