package com.gobrs.async.core.config;

import com.gobrs.async.core.common.exception.GobrsAsyncException;
import com.gobrs.async.core.holder.BeanHolder;
import com.gobrs.async.core.rule.Rule;

import java.util.Objects;

/**
 * The type Config manager.
 *
 * @program: gobrs -async
 * @ClassName ConfigManager
 * @description:
 * @author: sizegang
 * @create: 2022 -10-29
 */
public class ConfigManager {


    public static ConfigFactory configFactory = BeanHolder.getBean(ConfigFactory.class);

    /**
     * Config instance config factory.
     *
     * @return the config factory
     */
    public static ConfigFactory configInstance() {
        return checkAndGet();
    }

    /**
     * Gets global config.
     *
     * @return the global config
     */
    public static GobrsAsyncProperties getGlobalConfig() {
        return checkAndGet().getGobrsAsyncProperties();
    }

    /**
     * Gets rule.
     *
     * @param ruleName the rule name
     * @return the rule
     */
    public static Rule getRule(String ruleName) {
        return checkAndGet().getProcessRules().get(ruleName);
    }

    /**
     * @return
     */
    private static ConfigFactory checkAndGet() {
        if (Objects.isNull(configFactory)) {
            configFactory = BeanHolder.getBean(ConfigFactory.class);
            if (Objects.isNull(configFactory)) {
                throw new GobrsAsyncException("ConfigFactory is null");
            }
        }
        return configFactory;
    }


    public void addRule(String ruleName, Rule rule) {
        configFactory.addRule(ruleName, rule);
    }

}
