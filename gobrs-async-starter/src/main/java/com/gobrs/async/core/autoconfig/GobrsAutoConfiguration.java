package com.gobrs.async.core.autoconfig;

import com.gobrs.async.core.*;
import com.gobrs.async.core.property.GobrsAsyncProperties;
import com.gobrs.async.core.callback.*;
import com.gobrs.async.core.config.*;
import com.gobrs.async.core.engine.RuleEngine;
import com.gobrs.async.core.engine.RuleParseEngine;
import com.gobrs.async.core.engine.RulePostProcessor;
import com.gobrs.async.core.engine.RuleThermalLoad;
import com.gobrs.async.core.holder.BeanHolder;
import com.gobrs.async.core.threadpool.GobrsAsyncThreadPoolFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.gobrs.async.core.autoconfig.GobrsAutoConfiguration.GOBRS_NAMESPACE;


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
@AutoConfigureAfter({GobrsPropertyAutoConfiguration.class})
@ConditionalOnProperty(prefix = GobrsAsyncProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
@Import(BeanHolder.class)
@ComponentScan(value = GOBRS_NAMESPACE)
public class GobrsAutoConfiguration {

    static {
        Environment.env();
    }

    /**
     * The constant GOBRS_NAMESPACE.
     */
    protected static final String GOBRS_NAMESPACE = "com.gobrs.async";

    /**
     * Instantiates a new Gobrs auto configuration.
     */
    public GobrsAutoConfiguration() {
    }

    private GobrsConfig gobrsConfig;

    /**
     * Instantiates a new Gobrs auto configuration.
     *
     * @param gobrsConfig the gobrs config
     */
    public GobrsAutoConfiguration(GobrsConfig gobrsConfig) {
        this.gobrsConfig = gobrsConfig;
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
     * @param gobrsConfig the gobrs config
     * @return the gobrs async thread pool factory
     */
    @Bean
    public GobrsAsyncThreadPoolFactory gobrsAsyncThreadPoolFactory(GobrsConfig gobrsConfig) {
        return new GobrsAsyncThreadPoolFactory(gobrsConfig);
    }


    /**
     * Rule engine rule engine.
     *
     * @param gobrsConfig the gobrs config
     * @param gobrsAsync  the gobrs async
     * @return the rule engine
     */
    @Bean
    @ConditionalOnMissingBean(value = RuleEngine.class)
    public RuleEngine ruleEngine(GobrsConfig gobrsConfig, GobrsAsync gobrsAsync) {
        return new RuleParseEngine(gobrsConfig, gobrsAsync);
    }

    /**
     * Config factory config factory.
     *
     * @param gobrsConfig the gobrs config
     * @return the config factory
     */
    @Bean
    public ConfigFactory configFactory(GobrsConfig gobrsConfig) {
        return new ConfigFactory(gobrsConfig);
    }

    /**
     * Config manager config manager.
     *
     * @return the config manager
     */
    @ConditionalOnBean(ConfigFactory.class)
    @Bean
    public ConfigManager configManager() {
        return new ConfigManager();
    }

    /**
     * Rule engine post processor rule post processor.
     *
     * @param configManager the config manager
     * @return the rule post processor
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
     * @return the bean holder
     */
    @Bean
    public BeanHolder gobrsSpring() {
        return new BeanHolder();
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
