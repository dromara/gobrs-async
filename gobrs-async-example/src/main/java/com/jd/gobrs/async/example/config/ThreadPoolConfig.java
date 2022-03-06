package com.jd.gobrs.async.example.config;

import com.jd.gobrs.async.threadpool.GobrsAsyncThreadPoolFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @program: gobrs-async
 * @ClassName ThreadPoolConfig
 * @description:
 * @author: sizegang
 * @create: 2022-02-20 00:34
 * @Version 1.0
 **/
@Configuration
public class ThreadPoolConfig {

    @Autowired
    private GobrsAsyncThreadPoolFactory factory;

    private ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    @Bean
    public ThreadPoolTaskExecutor gobrsThreadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setBeanName("jd-thead-pool");
        executor.setCorePoolSize(1000);//配置核心线程数
        executor.setMaxPoolSize(2000);//配置最大线程数
        executor.setKeepAliveSeconds(30);
        executor.setQueueCapacity(10000);//配置队列大小
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());//拒绝策略
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();//执行初始化

        factory.setThreadPoolExecutor(threadPoolExecutor);
        return executor;
    }


}
