package com.jd.platform.gobrs.async.autoconfig;


import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: gobrs
 * @ClassName BootstrapProperties
 * @description:
 * @author: sizegang
 * @create: 2022-01-08 17:30
 * @Version 1.0
 **/
@ConfigurationProperties(prefix = GobrsAsyncProperties.PREFIX)
public class GobrsAsyncProperties {

    public static final String PREFIX = "spring.gobrs.async";

    private String rules;

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }
}