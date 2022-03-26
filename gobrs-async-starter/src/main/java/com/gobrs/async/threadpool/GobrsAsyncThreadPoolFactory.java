package com.gobrs.async.threadpool;

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
    private static final ThreadPoolExecutor COMMON_POOL = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    private ThreadPoolExecutor threadPoolExecutor = defaultThreadPool();

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }


    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    private ThreadPoolExecutor defaultThreadPool() {
        return COMMON_POOL;
    }

}
