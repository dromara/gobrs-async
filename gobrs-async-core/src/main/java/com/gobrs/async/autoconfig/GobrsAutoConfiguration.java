package com.gobrs.async.autoconfig;

import com.jd.gobrs.async.engine.RuleParseEngine;
import com.jd.gobrs.async.engine.RulePostProcessor;
import com.jd.gobrs.async.exception.AsyncExceptionInterceptor;
import com.jd.gobrs.async.exception.GlobalAsyncExceptionInterceptor;
import com.jd.gobrs.async.gobrs.GobrsTaskFlow;
import com.jd.gobrs.async.spring.GobrsSpring;
import com.jd.gobrs.async.threadpool.GobrsAsyncThreadPoolFactory;
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

    /**
     * spring工具
     *
     * @return
     */
    @Bean
    public GobrsSpring gobrsSpring() {
        return new GobrsSpring();
    }

    /**
     * 规则引擎
     *
     * @return
     */
    @Bean
    public RuleParseEngine ruleParseEngine() {
        return new RuleParseEngine();
    }

    @Bean
    public RulePostProcessor ruleEnginePostProcessor() {
        return new RulePostProcessor();
    }

    @Bean
    public GobrsTaskFlow gobrsTaskFlow() {
        return new GobrsTaskFlow();
    }

    @Bean
    public GobrsAsyncThreadPoolFactory gobrsAsyncThreadPoolFactory() {
        return new GobrsAsyncThreadPoolFactory();
    }


    @Bean
    @ConditionalOnMissingBean(value = AsyncExceptionInterceptor.class)
    public AsyncExceptionInterceptor asyncExceptionInterceptor() {
        return new GlobalAsyncExceptionInterceptor();
    }
}
