package com.gobrs.async.core;

import com.gobrs.async.core.common.domain.TaskResult;
import com.gobrs.async.core.log.LogWrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * The type Task support.
 *
 * @program: gobrs -async-starter
 * @ClassName com.gobrs.async.TaskSupport
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

    /**
     * 执行线程池
     * The Executor service.
     */
    public ExecutorService executorService;

    /**
     * 日志封装
     */
    private LogWrapper logWrapper;


    /**
     * 任务参数封装
     * The com.gobrs.async.com.gobrs.async.test.task parameters
     */
    private Object param;

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
     * Gets com.gobrs.async.com.gobrs.async.test.task loader.
     *
     * @return the com.gobrs.async.com.gobrs.async.test.task loader
     */
    public TaskLoader getTaskLoader() {
        return taskLoader;
    }

    /**
     * Sets com.gobrs.async.com.gobrs.async.test.task loader.
     *
     * @param taskLoader the com.gobrs.async.com.gobrs.async.test.task loader
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
     * Gets com.gobrs.async.log wrapper.
     *
     * @return the com.gobrs.async.log wrapper
     */
    public LogWrapper getLogWrapper() {
        return logWrapper;
    }

    /**
     * Sets com.gobrs.async.log wrapper.
     *
     * @param logWrapper the com.gobrs.async.log wrapper
     */
    public void setLogWrapper(LogWrapper logWrapper) {
        this.logWrapper = logWrapper;
    }


}
