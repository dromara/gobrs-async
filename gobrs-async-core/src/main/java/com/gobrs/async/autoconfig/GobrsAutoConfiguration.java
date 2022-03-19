package com.gobrs.async.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
}
