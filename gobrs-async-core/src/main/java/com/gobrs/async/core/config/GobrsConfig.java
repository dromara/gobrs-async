package com.gobrs.async.core.config;

import com.gobrs.async.core.common.constant.ConfigPropertiesConstant;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.gobrs.async.core.common.def.DefaultConfig.*;

/**
 * The type Gobrs config.
 *
 * @program: gobrs -async
 * @ClassName GobrsConfig
 * @description:
 * @author: sizegang
 * @create: 2022 -12-10
 */
@Data
public class GobrsConfig {

    /**
     * The constant PREFIX.
     */
    public static final String PREFIX = ConfigPropertiesConstant.PREFIX;

    private boolean enable;

    /**
     * Task rules
     */
    private List<GobrsAsyncRule> rules;

    /**
     * Task separator
     */
    private String split = ";";

    /**
     * Next com.gobrs.async.com.gobrs.async.test.task
     */
    private String point = "->";


    private ThreadPool threadPool;

    /**
     * 超时时间监听时间
     */
    private Integer timeoutCoreSize;


    /**
     * Whether global parameter dataContext mode  Parameter context
     */
    private boolean paramContext = true;



    /**
     * Default timeout
     *
     * @return
     */
    private long timeout = 3000;


    private boolean relyDepend = false;


    /**
     * The type Thread pool.
     */
    @Data
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

    }
}
