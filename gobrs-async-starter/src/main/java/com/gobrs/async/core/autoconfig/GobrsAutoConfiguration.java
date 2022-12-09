package com.gobrs.async.core.autoconfig;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.core.TaskFlow;
import com.gobrs.async.core.config.*;
import com.gobrs.async.core.callback.*;
import com.gobrs.async.core.engine.RuleEngine;
import com.gobrs.async.core.engine.RuleParseEngine;
import com.gobrs.async.core.engine.RulePostProcessor;
import com.gobrs.async.core.engine.RuleThermalLoad;
import com.gobrs.async.core.holder.BeanHolder;
import com.gobrs.async.core.threadpool.GobrsAsyncThreadPoolFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * The type Gobrs auto configuration.
 *
 * @program: gobrs
 * @ClassName GobrsAutoConfiguration auto com.gobrs.async.config
 * @description:
 * @author: sizegang
 * @create: 2022 -01-08 18:21
 * @Version 1.0
 */
@Configuration
@EnableConfigurationProperties({GobrsAsyncProperties.class, RuleConfig.class, LogConfig.class})
@ConditionalOnProperty(prefix = GobrsAsyncProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
public class GobrsAutoConfiguration {


    /**
     * Instantiates a new Gobrs auto configuration.
     */
    public GobrsAutoConfiguration() {
    }

    private GobrsAsyncProperties gobrsAsyncProperties;


    /**
     * Instantiates a new Gobrs auto configuration.
     *
     * @param gobrsAsyncProperties the gobrs async properties
     */
    public GobrsAutoConfiguration(GobrsAsyncProperties gobrsAsyncProperties) {
        this.gobrsAsyncProperties = gobrsAsyncProperties;
    }

    /**
     * Task flow com.gobrs.async.com.gobrs.async.test.task flow.
     *
     * @return the com.gobrs.async.com.gobrs.async.test.task flow
     */
    @Bean
    public TaskFlow taskFlow() {
        return new TaskFlow();
    }

    /**
     * Gobrs async thread pool factory gobrs async thread pool factory.
     *
     * @param grabrsAsyncProperties the grabrs async properties
     * @return the gobrs async thread pool factory
     */
    @Bean
    public GobrsAsyncThreadPoolFactory gobrsAsyncThreadPoolFactory(GobrsAsyncProperties grabrsAsyncProperties) {
        return new GobrsAsyncThreadPoolFactory(grabrsAsyncProperties);
    }


    /**
     * Rule com.gobrs.async.engine com.gobrs.async.rule com.gobrs.async.engine.
     *
     * @return the com.gobrs.async.rule com.gobrs.async.engine
     */
    @Bean
    @ConditionalOnMissingBean(value = RuleEngine.class)
    public RuleEngine ruleEngine() {
        return new RuleParseEngine<>();
    }

    /**
     * Config factory config factory.
     *
     * @param gobrsAsyncProperties the gobrs async properties
     * @return the config factory
     */
    @Bean
    public ConfigFactory configFactory(GobrsAsyncProperties gobrsAsyncProperties) {
        return new ConfigFactory(gobrsAsyncProperties);
    }

    /**
     * Config cache manager config cache manager.
     *
     * @return the config cache manager
     */
    @ConditionalOnBean(ConfigFactory.class)
    @Bean
    public ConfigManager configManager() {
        return new ConfigManager();
    }


    /**
     * Rule com.gobrs.async.engine post processor com.gobrs.async.rule post processor.
     *
     * @param configManager the config manager
     * @return the com.gobrs.async.rule post processor
     */
    @Bean
    public RulePostProcessor ruleEnginePostProcessor(ConfigManager configManager) {
        return new RulePostProcessor(configManager);
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
     * Gobrs spring bean holder.
     *
     * @return the gobrs com.gobrs.async.spring
     */
    @Bean
    public BeanHolder gobrsSpring() {
        return new BeanHolder();
    }

    /**
     * Async com.gobrs.async.exception interceptor async com.gobrs.async.com.gobrs.async.test.task com.gobrs.async.exception interceptor.
     *
     * @return the async com.gobrs.async.com.gobrs.async.test.task com.gobrs.async.exception interceptor
     */
    @Bean
    @ConditionalOnMissingBean(value = AsyncTaskExceptionInterceptor.class)
    public AsyncTaskExceptionInterceptor asyncExceptionInterceptor() {
        return new DefaultAsyncExceptionInterceptor();
    }

    /**
     * Async com.gobrs.async.com.gobrs.async.test.task pre interceptor async com.gobrs.async.com.gobrs.async.test.task pre interceptor.
     *
     * @return the async com.gobrs.async.com.gobrs.async.test.task pre interceptor
     */
    @Bean
    @ConditionalOnMissingBean(value = AsyncTaskPreInterceptor.class)
    public AsyncTaskPreInterceptor asyncTaskPreInterceptor() {
        return new DefaultAsyncTaskPreInterceptor();
    }

    /**
     * Async com.gobrs.async.com.gobrs.async.test.task post interceptor async com.gobrs.async.com.gobrs.async.test.task post interceptor.
     *
     * @return the async com.gobrs.async.com.gobrs.async.test.task post interceptor
     */
    @Bean
    @ConditionalOnMissingBean(value = AsyncTaskPostInterceptor.class)
    public AsyncTaskPostInterceptor asyncTaskPostInterceptor() {
        return new DefaultAsyncTaskPostInterceptor();
    }

    /**
     * Rule thermal loading com.gobrs.async.rule thermal load.
     *
     * @return the com.gobrs.async.rule thermal load
     */
    @Bean
    public RuleThermalLoad ruleThermalLoading() {
        return new RuleThermalLoad();
    }
}
