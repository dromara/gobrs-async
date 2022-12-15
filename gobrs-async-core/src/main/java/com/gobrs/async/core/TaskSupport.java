package com.gobrs.async.core;

import com.gobrs.async.core.common.domain.TaskResult;
import com.gobrs.async.core.common.domain.TaskStatus;
import com.gobrs.async.core.log.LogWrapper;
import com.gobrs.async.core.task.AsyncTask;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

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
    private Object param;

    /**
     * Task result encapsulation
     * 任务结果
     */
    private Map<Class, TaskResult> resultMap = new ConcurrentHashMap<>();

    private Map<Class, TaskStatus> taskStatus = new ConcurrentHashMap<>();

    /**
     * Gets status.
     *
     * @param clazz the clazz
     * @return the status
     */
    public TaskStatus getStatus(Class clazz) {
        return taskStatus.computeIfAbsent(clazz, TaskStatus::new);
    }


}
