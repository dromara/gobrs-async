package com.gobrs.async.core.config;


import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.gobrs.async.core.rule.Rule;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static com.gobrs.async.core.common.def.DefaultConfig.*;

/**
 * The type Gobrs async properties.
 *
 * @author sizegang1
 * @program: gobrs
 * @ClassName BootstrapProperties
 * @description:
 * @author: sizegang
 * @create: 2022 -01-08 17:30
 * @Version 1.0
 * @date 2022 -01-27 22:04
 */
@ConfigurationProperties(prefix = GobrsAsyncProperties.PREFIX)
@Component
public class GobrsAsyncProperties {

    /**
     * The constant PREFIX.
     */
    public static final String PREFIX = "com.gobrs.async.spring.gobrs.async";

    /**
     * Task rules
     */
    private List<Rule> rules;

    /**
     * Task separator
     */
    private String split = ";";

    /**
     * Next com.gobrs.async.com.gobrs.async.test.task
     */
    private String point = "->";


    private ThreadPool threadPool = new ThreadPool();


    /**
     * Whether global parameter dataContext mode  Parameter context
     */
    private boolean paramContext = true;

    /**
     * Whether transaction com.gobrs.async.com.gobrs.async.test.task
     */
    private boolean transaction = false;

    /**
     * Default timeout
     *
     * @return
     */
    private long timeout = 3000;


    private boolean relyDepend = false;


    /**
     * Gets timeout.
     *
     * @return the timeout
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Sets timeout.
     *
     * @param timeout the timeout
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets split.
     *
     * @return the split
     */
    public String getSplit() {

        return split;
    }

    /**
     * Sets split.
     *
     * @param split the split
     */
    public void setSplit(String split) {
        this.split = split;
    }

    /**
     * Gets point.
     *
     * @return the point
     */
    public String getPoint() {
        return point;
    }

    /**
     * Sets point.
     *
     * @param point the point
     */
    public void setPoint(String point) {
        this.point = point;
    }


    /**
     * Gets rules.
     *
     * @return the rules
     */
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * Sets rules.
     *
     * @param rules the rules
     */
    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }


    /**
     * Is rely depend boolean.
     *
     * @return the boolean
     */
    public boolean isRelyDepend() {
        return relyDepend;
    }

    /**
     * Sets rely depend.
     *
     * @param relyDepend the rely depend
     */
    public void setRelyDepend(boolean relyDepend) {
        this.relyDepend = relyDepend;
    }


    /**
     * Is param context boolean.
     *
     * @return the boolean
     */
    public boolean isParamContext() {
        return paramContext;
    }

    /**
     * Sets param context.
     *
     * @param paramContext the param context
     */
    public void setParamContext(boolean paramContext) {
        this.paramContext = paramContext;
    }

    /**
     * Is transaction boolean.
     *
     * @return the boolean
     */
    public boolean isTransaction() {
        return transaction;
    }

    /**
     * Sets transaction.
     *
     * @param transaction the transaction
     */
    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }

    /**
     * Gets thread pool.
     *
     * @return the thread pool
     */
    public ThreadPool getThreadPool() {
        return threadPool;
    }

    /**
     * Sets thread pool.
     *
     * @param threadPool the thread pool
     */
    public void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    /**
     * The type Thread pool.
     */
    public static class ThreadPool {
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
         * Reject com.gobrs.async.com.gobrs.async.test.task policy when thread pool com.gobrs.async.com.gobrs.async.test.task is full
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
}