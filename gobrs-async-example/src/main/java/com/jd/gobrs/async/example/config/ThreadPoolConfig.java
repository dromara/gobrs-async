package com.jd.gobrs.async.example.config;

import com.jd.gobrs.async.threadpool.GobrsAsyncThreadPoolFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

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


    @PostConstruct
    public void gobrsThreadPoolExecutor(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(300, 500, 30, TimeUnit.SECONDS,
                new LinkedBlockingQueue());
        factory.setThreadPoolExecutor(threadPoolExecutor);
    }

}
