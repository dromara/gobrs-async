package com.gobrs.async.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @program: gobrs-async-starter
 * @ClassName GobrsAsyncThreadPoolFactory
 * @description: Thread pool factory
 * @author: sizegang
 * @create: 2022-02-20
 * @Version 1.0
 **/
public class GobrsAsyncThreadPoolFactory {

    /**
     * The default thread pool is not long
     */
    private static final ExecutorService COMMON_POOL = Executors.newCachedThreadPool();

    private ExecutorService threadPoolExecutor = defaultThreadPool();

    public ExecutorService getThreadPoolExecutor() {
        return threadPoolExecutor;
    }


    public void setThreadPoolExecutor(ExecutorService threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    private ExecutorService defaultThreadPool() {
        return COMMON_POOL;
    }

}
