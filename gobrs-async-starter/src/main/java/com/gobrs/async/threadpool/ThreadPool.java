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
     * 核心线程数量
     */
    private Integer corePoolSize = calculateCoreNum();

    /**
     * 最大线程数量
     */
    private Integer maxPoolSize = corePoolSize + (corePoolSize >> 1);

    /**
     * 线程存活时间
     */
    private Long keepAliveTime = KEEPALIVETIME;

    /**
     * 线程存活时间单位
     */
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * 线程执行超时时间
     */
    private Long executeTimeOut = EXECUTETIMEOUT;

    /**
     * 队列最大容量
     */
    private Integer capacity = THREADPOOLQUEUESIZE;


    /**
     * 阻塞队列
     */
    private BlockingQueue workQueue = new LinkedBlockingQueue(capacity);

    /**
     * 线程池任务满时拒绝任务策略
     */
    private String rejectedExecutionHandler = "AbortPolicy";

    /**
     * 线程名称前缀
     */
    private String threadNamePrefix;


    /**
     * 允许核心线程超时
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
