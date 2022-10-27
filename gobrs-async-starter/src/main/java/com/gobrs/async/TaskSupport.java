package com.gobrs.async;

import com.gobrs.async.domain.TaskResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
     * 任务加载器
     * The Task loader.
     */
    public TaskLoader taskLoader;

    /** 执行线程池
     * The Executor service.
     */
    public ExecutorService executorService;

    /**
     * 任务参数封装
     * The task parameters
     */
    private Object param;

    private long traceId;


    /**
     * Task result encapsulation
     * 任务结果
     */
    private Map<Class, TaskResult> resultMap = new ConcurrentHashMap<>();


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

    /**
     * Gets trace id.
     *
     * @return the trace id
     */
    public long getTraceId() {
        return traceId;
    }

    /**
     * Sets trace id.
     *
     * @param traceId the trace id
     */
    public void setTraceId(long traceId) {
        this.traceId = traceId;
    }
}
