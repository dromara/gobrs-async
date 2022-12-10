package com.gobrs.async.core.threadpool;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.gobrs.async.core.config.GobrsConfig;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * The type Gobrs async thread pool factory.
 *
 * @program: gobrs -async-starter
 * @ClassName GobrsAsyncThreadPoolFactory
 * @description: Thread pool factory
 * @author: sizegang
 * @create: 2022 -02-20
 * @Version 1.0
 */
public class GobrsAsyncThreadPoolFactory {

    private GobrsConfig gobrsConfig;

    /**
     * Instantiates a new Gobrs async thread pool factory.
     */
    public GobrsAsyncThreadPoolFactory(GobrsConfig gobrsConfig) {
        this.gobrsConfig = gobrsConfig;
        this.COMMON_POOL = TtlExecutors.getTtlExecutorService(createDefaultThreadPool());
        this.threadPoolExecutor = defaultThreadPool();
    }

    /**
     * The default thread pool is not long
     */
    private final ExecutorService COMMON_POOL;

    private ExecutorService threadPoolExecutor;

    /**
     * Gets thread pool executor.
     *
     * @return the thread pool executor
     */
    public ExecutorService getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    /**
     * The user dynamically sets the thread pool parameters
     *
     * @param threadPoolExecutor the thread pool executor
     */
    public void setThreadPoolExecutor(ExecutorService threadPoolExecutor) {
        this.threadPoolExecutor = TtlExecutors.getTtlExecutorService(threadPoolExecutor);
    }

    private ExecutorService defaultThreadPool() {
        return COMMON_POOL;
    }

    /**
     * Create default thread pool thread pool executor.
     * 创建默认线程池
     *
     * @return the thread pool executor
     */
    ThreadPoolExecutor createDefaultThreadPool() {
        GobrsConfig.ThreadPool threadPool = gobrsConfig.getThreadPool();
        if (Objects.isNull(threadPool)) {
            return (ThreadPoolExecutor) Executors.newCachedThreadPool();
        }
        return new ThreadPoolExecutor(threadPool.getCorePoolSize(),
                threadPool.getMaxPoolSize(), threadPool.getKeepAliveTime(), threadPool.getTimeUnit(),
                threadPool.getWorkQueue(), ThreadPoolBuilder.caseReject(threadPool.getRejectedExecutionHandler()));
    }


}
