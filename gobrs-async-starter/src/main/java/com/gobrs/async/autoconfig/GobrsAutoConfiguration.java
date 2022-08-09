package com.gobrs.async.autoconfig;

import com.gobrs.async.GobrsAsync;
import com.gobrs.async.GobrsPrint;
import com.gobrs.async.TaskFlow;
import com.gobrs.async.callback.*;
import com.gobrs.async.engine.RuleEngine;
import com.gobrs.async.engine.RuleParseEngine;
import com.gobrs.async.engine.RulePostProcessor;
import com.gobrs.async.engine.RuleThermalLoad;
import com.gobrs.async.spring.GobrsSpring;
import com.gobrs.async.threadpool.GobrsAsyncThreadPoolFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * The type Gobrs auto configuration.
 *
 * @program: gobrs
 * @ClassName GobrsAutoConfiguration auto config
 * @description:
 * @author: sizegang
 * @create: 2022 -01-08 18:21
 * @Version 1.0
 */
@Configuration
@EnableConfigurationProperties(GobrsAsyncProperties.class)
@ConditionalOnProperty(prefix = GobrsAsyncProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
public class GobrsAutoConfiguration {


    /**
     * Instantiates a new Gobrs auto configuration.
     */
    public GobrsAutoConfiguration() {
    }

    private GobrsAsyncProperties properties;

    /**
     * Instantiates a new Gobrs auto configuration.
     *
     * @param properties the properties
     */
    public GobrsAutoConfiguration(GobrsAsyncProperties properties) {
        this.properties = properties;
    }

    /**
     * Task flow task flow.
     *
     * @return the task flow
     */
    @Bean
    public TaskFlow taskFlow() {
        return new TaskFlow();
    }

    /**
     * Gobrs async thread pool factory gobrs async thread pool factory.
     *
     * @return the gobrs async thread pool factory
     */
    @Bean
    public GobrsAsyncThreadPoolFactory gobrsAsyncThreadPoolFactory() {
        return new GobrsAsyncThreadPoolFactory();
    }


    /**
     * Rule engine rule engine.
     *
     * @return the rule engine
     */
    @Bean
    @ConditionalOnMissingBean(value = RuleEngine.class)
    public RuleEngine ruleEngine() {
        return new RuleParseEngine<>();
    }


    /**
     * Rule engine post processor rule post processor.
     *
     * @return the rule post processor
     */
    @Bean
    public RulePostProcessor ruleEnginePostProcessor() {
        return new RulePostProcessor();
    }


    /**
     * Gobrs async gobrs async.
     *
     * @return the gobrs async
     */
    @Bean
    public GobrsAsync gobrsAsync() {
        return new GobrsAsync();
    }

    /**
     * Gobrs spring gobrs spring.
     *
     * @return the gobrs spring
     */
    @Bean
    public GobrsSpring gobrsSpring() {
        return new GobrsSpring();
    }

    /**
     * Async exception interceptor async task exception interceptor.
     *
     * @return the async task exception interceptor
     */
    @Bean
    @ConditionalOnMissingBean(value = AsyncTaskExceptionInterceptor.class)
    public AsyncTaskExceptionInterceptor asyncExceptionInterceptor() {
        return new DefaultAsyncExceptionInterceptor();
    }

    /**
     * Async task pre interceptor async task pre interceptor.
     *
     * @return the async task pre interceptor
     */
    @Bean
    @ConditionalOnMissingBean(value = AsyncTaskPreInterceptor.class)
    public AsyncTaskPreInterceptor asyncTaskPreInterceptor() {
        return new DefaultAsyncTaskPreInterceptor();
    }

    /**
     * Async task post interceptor async task post interceptor.
     *
     * @return the async task post interceptor
     */
    @Bean
    @ConditionalOnMissingBean(value = AsyncTaskPostInterceptor.class)
    public AsyncTaskPostInterceptor asyncTaskPostInterceptor() {
        return new DefaultAsyncTaskPostInterceptor();
    }

    /**
     * Rule thermal loading rule thermal load.
     *
     * @return the rule thermal load
     */
    @Bean
    public RuleThermalLoad ruleThermalLoading() {
        return new RuleThermalLoad();
    }
}
