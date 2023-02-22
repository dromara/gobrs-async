package com.gobrs.async.plugin.hippo4j.config;

import cn.hippo4j.core.enable.EnableDynamicThreadPool;
import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import com.gobrs.async.plugin.base.threadpool.ThreadPoolCreator;
import com.gobrs.async.plugin.hippo4j.creator.Hippo4jThreadPoolCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * The type Hippo 4 j auto configuration.
 *
 * @program: gobrs -async
 * @ClassName Hippo4jAutoConfiguration
 * @description:
 * @author: sizegang
 * @create: 2023 -01-05
 */
@Configuration
@EnableDynamicThreadPool
public class Hippo4jAutoConfiguration {

    private static final String threadPoolId = "Hipp4j-Gobrs-Async-threadpool";

    /**
     * Dy thread pool config dy thread pool config.
     *
     * @return the dy thread pool config
     */
    @Bean
    @DynamicThreadPool
    public ThreadPoolExecutor hippo4ThreadPool() {
        ThreadPoolExecutor messageConsumeDynamicExecutor = ThreadPoolBuilder.builder()
                .threadFactory(threadPoolId)
                .threadPoolId(threadPoolId)
                .dynamicPool()
                .build();
        return messageConsumeDynamicExecutor;
    }

    /**
     * Hippo 4 j thread pool creator hippo 4 j thread pool creator.
     *
     * @param hippo4ThreadPool the hippo 4 thread pool
     * @return the hippo 4 j thread pool creator
     */
    @Bean
    @ConditionalOnMissingBean(ThreadPoolCreator.class)
    public Hippo4jThreadPoolCreator hippo4jThreadPoolCreator(ThreadPoolExecutor hippo4ThreadPool) {
        return new Hippo4jThreadPoolCreator(hippo4ThreadPool);
    }


}
