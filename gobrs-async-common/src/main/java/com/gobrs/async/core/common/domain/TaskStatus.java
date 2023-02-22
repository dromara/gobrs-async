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
    private final String taskName;


    /**
     * Instantiates a new Task status.
     *
     * @param taskName the task name
     */
    public TaskStatus(String taskName) {
        this.taskName = taskName;
    }

    /**
     * the status of currentTask (TASK_INITIALIZE,TASK_FINISH,TASK_TIMEOUT)
     *
     * @return the status
     */
    public AtomicInteger getStatus() {
        return status;
    }

    /**
     * the retryCounts of currentTask
     *
     * @return the retry counts
     */
    public AtomicInteger getRetryCounts() {
        return retryCounts;
    }

    /**
     * change task status
     *
     * @param expect the expect
     * @param update the update
     * @return the boolean
     */
    public boolean compareAndSet(int expect, int update) {
        return getStatus().compareAndSet(expect, update);
    }

    @Override
    public String toString() {
        return taskName + "{" + "status=" + status + ", retryCounts=" + retryCounts + '}';
    }
}
