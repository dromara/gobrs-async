package com.gobrs.async.core.threadpool;

import com.gobrs.async.core.common.def.DefaultConfig;
import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.*;

/**
 * The type Thread pool.
 *
 * @program: gobrs -async-core
 * @ClassName ThreadPool
 * @description:
 * @author: sizegang
 * @create: 2022 -03-26
 */
@Data
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
    private Long keepAliveTime = DefaultConfig.KEEP_ALIVE_TIME;

    /**
     * thread survival time unit
     */
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * Thread execution timeout
     */
    private Long executeTimeOut = DefaultConfig.EXECUTE_TIMEOUT;

    /**
     * queue maximum capacity
     */
    private Integer capacity = DefaultConfig.THREAD_POOL_QUEUE_SIZE;


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

}
