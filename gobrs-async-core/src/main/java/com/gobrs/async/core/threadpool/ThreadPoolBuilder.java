package com.gobrs.async.core.threadpool;

import com.gobrs.async.core.common.constant.RejectedExecutionHandlerConstant;
import com.gobrs.async.core.common.def.DefaultConfig;
import com.gobrs.async.core.common.exception.GobrsAsyncException;

import java.math.BigDecimal;
import java.util.concurrent.*;

/**
 * The type Thread pool builder.
 *
 * @program: gobrs -async-core
 * @ClassName Builder
 * @description:
 * @author: sizegang
 * @create: 2022 -03-26
 */
public class ThreadPoolBuilder {

    /**
     * number of core threads
     */
    private int corePoolSize = calculateCoreNum();

    /**
     * maximum number of threads
     */
    private int maxPoolSize = corePoolSize + (corePoolSize >> 1);

    /**
     * thread survival time
     */
    private long keepAliveTime = 30000L;

    /**
     * thread survival time unit
     */
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * Thread execution timeout
     */
    private long executeTimeOut = 10000L;

    /**
     * queue maximum capacity
     */
    private int capacity = 512;


    /**
     * blocking queue
     */
    private BlockingQueue workQueue = new LinkedBlockingQueue(capacity);

    /**
     * Reject com.gobrs.async.com.gobrs.async.test.task policy when thread pool com.gobrs.async.com.gobrs.async.test.task is full
     */
    private RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();

    /**
     * Whether to guard the thread
     */
    private boolean isDaemon = false;

    /**
     * thread name prefix
     */
    private String threadNamePrefix;

    /**
     * Allow core threads to time out
     */
    private Boolean allowCoreThreadTimeOut = false;


    /**
     * Gets core pool size.
     *
     * @return the core pool size
     */
    public int getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * Core pool size thread pool builder.
     *
     * @param corePoolSize the core pool size
     * @return the thread pool builder
     */
    public ThreadPoolBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    /**
     * Gets max pool size.
     *
     * @return the max pool size
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * Max pool size thread pool builder.
     *
     * @param maxPoolSize the max pool size
     * @return the thread pool builder
     */
    public ThreadPoolBuilder maxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    /**
     * Gets keep alive time.
     *
     * @return the keep alive time
     */
    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    /**
     * Keep alive time thread pool builder.
     *
     * @param keepAliveTime the keep alive time
     * @return the thread pool builder
     */
    public ThreadPoolBuilder keepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
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
     * Time unit thread pool builder.
     *
     * @param timeUnit the time unit
     * @return the thread pool builder
     */
    public ThreadPoolBuilder timeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    /**
     * Gets execute time out.
     *
     * @return the execute time out
     */
    public long getExecuteTimeOut() {
        return executeTimeOut;
    }

    /**
     * Execute time out thread pool builder.
     *
     * @param executeTimeOut the execute time out
     * @return the thread pool builder
     */
    public ThreadPoolBuilder executeTimeOut(long executeTimeOut) {
        this.executeTimeOut = executeTimeOut;
        return this;
    }

    /**
     * Gets capacity.
     *
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Capacity thread pool builder.
     *
     * @param capacity the capacity
     * @return the thread pool builder
     */
    public ThreadPoolBuilder capacity(int capacity) {
        this.capacity = capacity;
        return this;
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
     * Work queue thread pool builder.
     *
     * @param workQueue the work queue
     * @return the thread pool builder
     */
    public ThreadPoolBuilder workQueue(BlockingQueue workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    /**
     * Gets rejected execution handler.
     *
     * @return the rejected execution handler
     */
    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }

    /**
     * Rejected execution handler thread pool builder.
     *
     * @param rejectedExecutionHandler the rejected execution handler
     * @return the thread pool builder
     */
    public ThreadPoolBuilder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        return this;
    }

    /**
     * Is daemon boolean.
     *
     * @return the boolean
     */
    public boolean isDaemon() {
        return isDaemon;
    }

    /**
     * Daemon thread pool builder.
     *
     * @param daemon the daemon
     * @return the thread pool builder
     */
    public ThreadPoolBuilder daemon(boolean daemon) {
        isDaemon = daemon;
        return this;
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
     * Thread name prefix thread pool builder.
     *
     * @param threadNamePrefix the thread name prefix
     * @return the thread pool builder
     */
    public ThreadPoolBuilder threadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
        return this;
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
     * Allow core thread time out thread pool builder.
     *
     * @param allowCoreThreadTimeOut the allow core thread time out
     * @return the thread pool builder
     */
    public ThreadPoolBuilder allowCoreThreadTimeOut(Boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }

    /**
     * Build thread pool executor.
     *
     * @return the thread pool executor
     */
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


    /**
     * Build by thread pool thread pool executor.
     *
     * @param pool the pool
     * @return the thread pool executor
     */
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
            pool.setCapacity(DefaultConfig.THREADPOOLQUEUESIZE);
        }
    }

    /**
     * Case reject rejected execution handler.
     *
     * @param rejected the rejected
     * @return the rejected execution handler
     */
    public static RejectedExecutionHandler caseReject(String rejected) {
        if (rejected == null) {
            return new ThreadPoolExecutor.AbortPolicy();
        }

        RejectedExecutionHandler rejectedExecutionHandler;
        switch (rejected) {
            case RejectedExecutionHandlerConstant
                    .CALLER_RUNSPOLICY:
                rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
                break;
            case RejectedExecutionHandlerConstant
                    .ABORT_POLICY:
                rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
                break;
            case RejectedExecutionHandlerConstant
                    .DISCARD_POLICY:
                rejectedExecutionHandler = new ThreadPoolExecutor.DiscardPolicy();
                break;
            case RejectedExecutionHandlerConstant
                    .DISCARDOLDEST_POLICY:
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
