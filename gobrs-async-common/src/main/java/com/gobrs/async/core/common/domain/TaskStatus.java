package com.gobrs.async.core.common.domain;

import java.util.concurrent.atomic.AtomicInteger;

import static com.gobrs.async.core.common.def.DefaultConfig.TASK_INITIALIZE;

/**
 * task运行时状态
 *
 * @author yuzd
 */
public class TaskStatus {
    /**
     * the status of currentTask (TASK_INITIALIZE,TASK_FINISH,TASK_TIMEOUT)
     */
    private final AtomicInteger status = new AtomicInteger(TASK_INITIALIZE);

    /**
     * the retryCounts of currentTask
     */
    private final AtomicInteger retryCounts = new AtomicInteger(0);


    /**
     * the class type of currentTask
     */
    private final Class<?> taskClass;


    public TaskStatus(Class<?> taskCls) {
        this.taskClass = taskCls;
    }

    /**
     * the status of currentTask (TASK_INITIALIZE,TASK_FINISH,TASK_TIMEOUT)
     */
    public AtomicInteger getStatus() {
        return status;
    }

    /**
     * the retryCounts of currentTask
     */
    public AtomicInteger getRetryCounts() {
        return retryCounts;
    }

    /**
     * change task status
     */
    public boolean compareAndSet(int expect, int update) {
        return getStatus().compareAndSet(expect, update);
    }

    @Override
    public String toString() {
        return taskClass.getSimpleName() + "{" + "status=" + status + ", retryCounts=" + retryCounts + '}';
    }
}
