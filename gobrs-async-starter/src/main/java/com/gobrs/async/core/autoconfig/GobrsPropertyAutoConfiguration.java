package com.gobrs.async.core.autoconfig;

import com.gobrs.async.core.config.GobrsAsyncRule;
import com.gobrs.async.core.property.GobrsAsyncProperties;
import com.gobrs.async.core.property.LogConfig;
import com.gobrs.async.core.property.RuleConfig;
import com.gobrs.async.core.config.GobrsConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Gobrs property auto configuration.
 *
 * @program: gobrs -async
 * @ClassName GobrsPropertyAutoConfiguration
 * @description:
 * @author: sizegang
 * @create: 2022 -12-10
 */
@Configuration
@EnableConfigurationProperties({GobrsAsyncProperties.class, RuleConfig.class, LogConfig.class})
public class GobrsPropertyAutoConfiguration {


    /**
     * Gobrs config gobrs config.
     *
     * @param properties the properties
     * @return the gobrs config
     */
    @Bean
    public GobrsConfig gobrsConfig(GobrsAsyncProperties properties) {

        GobrsConfig gobrsConfig = new GobrsConfig();
        BeanUtils.copyProperties(properties, gobrsConfig);
        List<RuleConfig> rules = properties.getRules();
        List<GobrsAsyncRule> rList = rules.stream().map(x -> {
            GobrsAsyncRule r = new GobrsAsyncRule();
            BeanUtils.copyProperties(x, r);
            return r;
        }).collect(Collectors.toList());

        gobrsConfig.setRules(rList);
        return gobrsConfig;
    }
}
