package com.gobrs.async.threadpool;

import com.gobrs.async.exception.GobrsAsyncException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.*;

import static com.gobrs.async.def.DefaultConfig.THREADPOOLQUEUESIZE;

/**
 * @program: gobrs-async-core
 * @ClassName Builder
 * @description:
 * @author: sizegang
 * @create: 2022-03-26
 **/
public class ThreadPoolBuilder {

    /**
     * 核心线程数量
     */
    private int corePoolSize = calculateCoreNum();

    /**
     * 最大线程数量
     */
    private int maxPoolSize = corePoolSize + (corePoolSize >> 1);

    /**
     * 线程存活时间
     */
    private long keepAliveTime = 30000L;

    /**
     * 线程存活时间单位
     */
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * 线程执行超时时间
     */
    private long executeTimeOut = 10000L;

    /**
     * 队列最大容量
     */
    private int capacity = 512;


    /**
     * 阻塞队列
     */
    private BlockingQueue workQueue = new LinkedBlockingQueue(capacity);

    /**
     * 线程池任务满时拒绝任务策略
     */
    private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();

    /**
     * 是否守护线程
     */
    private boolean isDaemon = false;

    /**
     * 线程名称前缀
     */
    private String threadNamePrefix;

    /**
     * 允许核心线程超时
     */
    private Boolean allowCoreThreadTimeOut = false;


    public int getCorePoolSize() {
        return corePoolSize;
    }

    public ThreadPoolBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public ThreadPoolBuilder maxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public ThreadPoolBuilder keepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public ThreadPoolBuilder timeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public long getExecuteTimeOut() {
        return executeTimeOut;
    }

    public ThreadPoolBuilder executeTimeOut(long executeTimeOut) {
        this.executeTimeOut = executeTimeOut;
        return this;
    }

    public int getCapacity() {
        return capacity;
    }

    public ThreadPoolBuilder capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public BlockingQueue getWorkQueue() {
        return workQueue;
    }

    public ThreadPoolBuilder workQueue(BlockingQueue workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }

    public ThreadPoolBuilder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        return this;
    }

    public boolean isDaemon() {
        return isDaemon;
    }

    public ThreadPoolBuilder daemon(boolean daemon) {
        isDaemon = daemon;
        return this;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public ThreadPoolBuilder threadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
        return this;
    }


    public Boolean getAllowCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }

    public ThreadPoolBuilder allowCoreThreadTimeOut(Boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }

    public ThreadPoolExecutor build() {
        ThreadPoolExecutor executor;
        try {
            executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, timeUnit, workQueue, rejectedExecutionHandler);
        } catch (Exception exception) {
            throw new GobrsAsyncException(String.format("Thread Pool Config Error %s", exception));
        }
        executor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
        return executor;
    }


    public static ThreadPoolExecutor buildByThreadPool(ThreadPool pool) {
        check(pool);
        ThreadPoolExecutor executor;
        try {
            executor = new ThreadPoolExecutor(pool.getCorePoolSize(), pool.getMaxPoolSize()
                    , pool.getKeepAliveTime(), pool.getTimeUnit(), pool.getWorkQueue(), caseReject(pool.getRejectedExecutionHandler()));
            executor.allowCoreThreadTimeOut(pool.getAllowCoreThreadTimeOut());
        } catch (Exception exception) {
            throw new GobrsAsyncException(String.format("Thread Pool Config Error %s", exception));
        }
        return executor;
    }

    private static void check(ThreadPool pool) {
        if (pool.getCorePoolSize() == null) {
            throw new GobrsAsyncException("thread pool coreSize empty");
        }
        if (pool.getMaxPoolSize() == null) {
            throw new GobrsAsyncException("thread pool maxSize empty");
        }
        if (pool.getKeepAliveTime() == null) {
            throw new GobrsAsyncException("thread pool keepAliveTime size empty");
        }
        if (pool.getCapacity() == null) {
            pool.setCapacity(THREADPOOLQUEUESIZE);
        }
    }

    public static RejectedExecutionHandler caseReject(String rejected) {
        if (rejected == null) {
            return new ThreadPoolExecutor.AbortPolicy();
        }

        RejectedExecutionHandler rejectedExecutionHandler;
        switch (rejected) {
            case "CallerRunsPolicy":
                rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
                break;
            case "AbortPolicy":
                rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
                break;
            case "DiscardPolicy":
                rejectedExecutionHandler = new ThreadPoolExecutor.DiscardPolicy();
                break;
            case "DiscardOldestPolicy":
                rejectedExecutionHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
                break;
            default:
                rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
        }
        return rejectedExecutionHandler;
    }


    private Integer calculateCoreNum() {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        return new BigDecimal(cpuCoreNum).divide(new BigDecimal("0.2")).intValue();
    }

}
