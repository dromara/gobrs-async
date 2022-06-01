package com.gobrs.async.threadpool;

import org.springframework.core.task.TaskDecorator;

import java.math.BigDecimal;
import java.util.concurrent.*;

import static com.gobrs.async.def.DefaultConfig.*;

/**
 * @program: gobrs-async-core
 * @ClassName ThreadPool
 * @description:
 * @author: sizegang
 * @create: 2022-03-26
 **/
public class ThreadPool {
    /**
     * number of core threads
     */
    private Integer corePoolSize = calculateCoreNum();

    /**
     * maximum number of threads
     */
    private Integer maxPoolSize = corePoolSize + (corePoolSize >> 1);

    /**
     * thread survival time
     */
    private Long keepAliveTime = KEEPALIVETIME;

    /**
     * thread survival time unit
     */
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * Thread execution timeout
     */
    private Long executeTimeOut = EXECUTETIMEOUT;

    /**
     * queue maximum capacity
     */
    private Integer capacity = THREADPOOLQUEUESIZE;


    /**
     * blocking queue
     */
    private BlockingQueue workQueue = new LinkedBlockingQueue(capacity);

    /**
     * Reject task policy when thread pool task is full
     */
    private String rejectedExecutionHandler = "AbortPolicy";

    /**
     * thread name prefix
     */
    private String threadNamePrefix;


    /**
     * Allow core threads to time out
     */
    private Boolean allowCoreThreadTimeOut = false;

    private Integer calculateCoreNum() {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        return new BigDecimal(cpuCoreNum).divide(new BigDecimal("0.2")).intValue();
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Long getExecuteTimeOut() {
        return executeTimeOut;
    }

    public void setExecuteTimeOut(long executeTimeOut) {
        this.executeTimeOut = executeTimeOut;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public BlockingQueue getWorkQueue() {
        return workQueue;
    }

    public void setWorkQueue(BlockingQueue workQueue) {
        this.workQueue = workQueue;
    }


    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }


    public Boolean getAllowCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }

    public void setAllowCoreThreadTimeOut(Boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }

    public String getRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }

    public void setRejectedExecutionHandler(String rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }
}
