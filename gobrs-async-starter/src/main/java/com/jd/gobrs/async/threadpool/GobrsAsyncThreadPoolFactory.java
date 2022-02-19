package com.jd.gobrs.async.threadpool;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @program: gobrs-async
 * @ClassName GobrsThreadPool
 * @description:
 * @author: sizegang
 * @create: 2022-02-20 00:39
 * @Version 1.0
 **/
public class GobrsAsyncThreadPoolFactory {

    private ThreadPoolTaskExecutor threadPoolExecutor = defaultThreadPool();

    private ThreadPoolTaskExecutor releaseThreadPoolExecutor = releaseThreadPool();

    public ThreadPoolTaskExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }


    public ThreadPoolTaskExecutor getReleaseThreadPoolExecutor() {
        return releaseThreadPoolExecutor;
    }

    public void setThreadPoolExecutor(ThreadPoolTaskExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    private ThreadPoolTaskExecutor defaultThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setBeanName("gobrs-threadpool");
        executor.setCorePoolSize(200);//配置核心线程数
        executor.setMaxPoolSize(400);//配置最大线程数
        executor.setKeepAliveSeconds(30);
        executor.setQueueCapacity(1000);//配置队列大小
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());//拒绝策略
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();//执行初始化
        return executor;
    }



    private ThreadPoolTaskExecutor releaseThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setBeanName("gobrs-release-threadpool");
        executor.setCorePoolSize(20);//配置核心线程数
        executor.setMaxPoolSize(40);//配置最大线程数
        executor.setKeepAliveSeconds(30);
        executor.setQueueCapacity(1000);//配置队列大小
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());//拒绝策略
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();//执行初始化
        return executor;
    }
}
