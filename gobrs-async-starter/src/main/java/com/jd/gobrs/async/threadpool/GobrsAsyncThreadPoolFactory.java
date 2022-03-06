package com.jd.gobrs.async.threadpool;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: gobrs-async
 * @ClassName GobrsThreadPool
 * @description:
 * @author: sizegang
 * @create: 2022-02-20 00:39
 * @Version 1.0
 **/
public class GobrsAsyncThreadPoolFactory {



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
