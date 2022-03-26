package com.gobrs.async.threadpool;

import org.springframework.core.task.TaskDecorator;

import java.math.BigDecimal;
import java.util.concurrent.*;

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
     * 线程任务装饰器
     */
    private TaskDecorator taskDecorator;

    /**
     * 等待终止毫秒
     */
    private Long awaitTerminationMillis = 5000L;

    /**
     * 等待任务在关机时完成
     */
    private Boolean waitForTasksToCompleteOnShutdown = true;

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

    public TaskDecorator getTaskDecorator() {
        return taskDecorator;
    }

    public ThreadPoolBuilder taskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
        return this;
    }

    public Long getAwaitTerminationMillis() {
        return awaitTerminationMillis;
    }

    public ThreadPoolBuilder awaitTerminationMillis(Long awaitTerminationMillis) {
        this.awaitTerminationMillis = awaitTerminationMillis;
        return this;
    }

    public Boolean getWaitForTasksToCompleteOnShutdown() {
        return waitForTasksToCompleteOnShutdown;
    }

    public ThreadPoolBuilder waitForTasksToCompleteOnShutdown(Boolean waitForTasksToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
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

        ThreadPoolExecutor executor = new ThreadPoolExecutor(maxPoolSize, corePoolSize, keepAliveTime, timeUnit, workQueue, rejectedExecutionHandler);
        executor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
        try {
            executor.awaitTermination(awaitTerminationMillis, timeUnit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return executor;
    }


    public static ThreadPoolExecutor buildByThreadPool(ThreadPool pool){

        ThreadPoolExecutor executor = new ThreadPoolExecutor(pool.getMaxPoolSize(), pool.getCorePoolSize()
                ,pool.getKeepAliveTime(), pool.getTimeUnit(), pool.getWorkQueue(), pool.getRejectedExecutionHandler());
        executor.allowCoreThreadTimeOut(pool.getAllowCoreThreadTimeOut());
        try {
            executor.awaitTermination(pool.getAwaitTerminationMillis(), pool.getTimeUnit());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return executor;
    }



    private Integer calculateCoreNum() {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        return new BigDecimal(cpuCoreNum).divide(new BigDecimal("0.2")).intValue();
    }

}
