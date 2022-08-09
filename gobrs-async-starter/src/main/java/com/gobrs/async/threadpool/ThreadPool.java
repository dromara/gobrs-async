package com.gobrs.async.threadpool;

import org.springframework.core.task.TaskDecorator;

import java.math.BigDecimal;
import java.util.concurrent.*;

import static com.gobrs.async.def.DefaultConfig.*;

/**
 * The type Thread pool.
 *
 * @program: gobrs -async-core
 * @ClassName ThreadPool
 * @description:
 * @author: sizegang
 * @create: 2022 -03-26
 */
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

    /**
     * Gets core pool size.
     *
     * @return the core pool size
     */
    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * Sets core pool size.
     *
     * @param corePoolSize the core pool size
     */
    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    /**
     * Gets max pool size.
     *
     * @return the max pool size
     */
    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * Sets max pool size.
     *
     * @param maxPoolSize the max pool size
     */
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    /**
     * Gets keep alive time.
     *
     * @return the keep alive time
     */
    public Long getKeepAliveTime() {
        return keepAliveTime;
    }

    /**
     * Sets keep alive time.
     *
     * @param keepAliveTime the keep alive time
     */
    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    /**
     * Gets time unit.
     *
     * @return the time unit
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    /**
     * Sets time unit.
     *
     * @param timeUnit the time unit
     */
    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    /**
     * Gets execute time out.
     *
     * @return the execute time out
     */
    public Long getExecuteTimeOut() {
        return executeTimeOut;
    }

    /**
     * Sets execute time out.
     *
     * @param executeTimeOut the execute time out
     */
    public void setExecuteTimeOut(long executeTimeOut) {
        this.executeTimeOut = executeTimeOut;
    }

    /**
     * Gets capacity.
     *
     * @return the capacity
     */
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * Sets capacity.
     *
     * @param capacity the capacity
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Gets work queue.
     *
     * @return the work queue
     */
    public BlockingQueue getWorkQueue() {
        return workQueue;
    }

    /**
     * Sets work queue.
     *
     * @param workQueue the work queue
     */
    public void setWorkQueue(BlockingQueue workQueue) {
        this.workQueue = workQueue;
    }


    /**
     * Gets thread name prefix.
     *
     * @return the thread name prefix
     */
    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    /**
     * Sets thread name prefix.
     *
     * @param threadNamePrefix the thread name prefix
     */
    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }


    /**
     * Gets allow core thread time out.
     *
     * @return the allow core thread time out
     */
    public Boolean getAllowCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }

    /**
     * Sets allow core thread time out.
     *
     * @param allowCoreThreadTimeOut the allow core thread time out
     */
    public void setAllowCoreThreadTimeOut(Boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }

    /**
     * Gets rejected execution handler.
     *
     * @return the rejected execution handler
     */
    public String getRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }

    /**
     * Sets rejected execution handler.
     *
     * @param rejectedExecutionHandler the rejected execution handler
     */
    public void setRejectedExecutionHandler(String rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }
}
