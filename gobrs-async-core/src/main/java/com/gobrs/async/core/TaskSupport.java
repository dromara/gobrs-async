package com.gobrs.async.core;

import com.gobrs.async.core.common.domain.AsyncParam;
import com.gobrs.async.core.common.domain.TaskResult;
import com.gobrs.async.core.common.domain.TaskStatus;
import com.gobrs.async.core.log.LogWrapper;
import com.gobrs.async.core.task.AsyncTask;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
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
@Data
@Accessors(chain = true)
public class TaskSupport {


    /**
     * 任务加载器
     * The Task loader.
     */
    public TaskLoader taskLoader;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 执行线程池
     * The Executor service.
     */
    public ExecutorService executorService;

    /**
     * 日志封装
     */
    private volatile LogWrapper logWrapper;


    /**
     * 任务参数封装
     * The com.gobrs.async.com.gobrs.async.test.task parameters
     */
    private AsyncParam param;

    /**
     * Task result encapsulation
     * 任务结果
     */
    private Map<String, TaskResult> resultMap = new ConcurrentHashMap<>();

    private Map<String, TaskStatus> taskStatus = new ConcurrentHashMap<>();

    /**
     * Gets status.
     *
     * @param taskName the task name
     * @return the status
     */
    public TaskStatus getStatus(String taskName) {
        return taskStatus.computeIfAbsent(taskName, TaskStatus::new);
    }

    /**
     * Gets result.
     *
     * @param <T>      the type parameter
     * @param taskName the task name
     * @param clazz    the clazz
     * @return the result
     */
    public <T> T getResult(String taskName, Class<T> clazz) {
        return (T) resultMap.get(taskName).getResult();
    }

    /**
     * Gets param.
     *
     * @param <T>      the type parameter
     * @param taskName the task name
     * @param clazz    the clazz
     * @return the param
     */
    public <T> T getParam(String taskName, Class<T> clazz) {
        Object o = param.get();
        if (o instanceof HashMap) {
            return (T) ((HashMap) o).get(taskName);
        }
        return (T) o;
    }


}
