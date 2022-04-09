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
 * @program: gobrs
 * @ClassName GobrsAutoConfiguration  auto config
 * @description:
 * @author: sizegang
 * @create: 2022-01-08 18:21
 * @Version 1.0
 **/
@Configuration
@EnableConfigurationProperties(GobrsAsyncProperties.class)
@ConditionalOnProperty(prefix = GobrsAsyncProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
public class GobrsAutoConfiguration {

    private final GobrsAsyncProperties properties;

    public GobrsAutoConfiguration(GobrsAsyncProperties properties) {
        this.properties = properties;
    }

    @Bean
    public TaskFlow taskFlow() {
        return new TaskFlow();
    }

    @Bean
    public GobrsAsyncThreadPoolFactory gobrsAsyncThreadPoolFactory() {
        return new GobrsAsyncThreadPoolFactory();
    }


    @Bean
    @ConditionalOnMissingBean(value = RuleEngine.class)
    public RuleEngine ruleEngine() {
        return new RuleParseEngine<>();
    }


    @Bean
    public RulePostProcessor ruleEnginePostProcessor() {
        return new RulePostProcessor();
    }


    @Bean
    public GobrsAsync gobrsAsync() {
        return new GobrsAsync();
    }

    @Bean
    public GobrsSpring gobrsSpring() {
        return new GobrsSpring();
    }

    @Bean
    @ConditionalOnMissingBean(value = AsyncTaskExceptionInterceptor.class)
    public AsyncTaskExceptionInterceptor asyncExceptionInterceptor() {
        return new DefaultAsyncExceptionInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean(value = AsyncTaskPreInterceptor.class)
    public AsyncTaskPreInterceptor asyncTaskPreInterceptor() {
        return new DefaultAsyncTaskPreInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean(value = AsyncTaskPostInterceptor.class)
    public AsyncTaskPostInterceptor asyncTaskPostInterceptor() {
        return new DefaultAsyncTaskPostInterceptor();
    }

    @Bean
    public RuleThermalLoad ruleThermalLoading() {
        return new RuleThermalLoad();
    }
}
