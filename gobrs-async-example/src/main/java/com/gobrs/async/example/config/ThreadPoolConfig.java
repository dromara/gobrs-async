package com.gobrs.async.example.config;

import com.gobrs.async.threadpool.GobrsAsyncThreadPoolFactory;
import com.gobrs.async.threadpool.GobrsThreadPoolConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The type Thread pool config.
 *
 * @program: gobrs -async
 * @ClassName ThreadPoolConfig
 * @description: Custom thread pool configuration
 * @author: sizegang
 * @create: 2022 -02-20 00:34
 * @Version 1.0
 */
@Configuration
public class ThreadPoolConfig extends GobrsThreadPoolConfiguration {

    @Override
    protected void doInitialize(GobrsAsyncThreadPoolFactory factory) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(300, 500, 30, TimeUnit.SECONDS,
                new LinkedBlockingQueue());
        factory.setThreadPoolExecutor(threadPoolExecutor);
    }
}
