package com.gobrs.async.core.config;

import com.gobrs.async.core.common.exception.GobrsAsyncException;
import com.gobrs.async.core.holder.BeanHolder;

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


    /**
     * The constant configFactory.
     */
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
    public static RuleConfig getRule(String ruleName) {
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


    /**
     * Add rule.
     *
     * @param ruleName the rule name
     * @param rule     the rule
     */
    public void addRule(String ruleName, RuleConfig rule) {
        configFactory.addRule(ruleName, rule);
    }


    /**
     * The type Action.
     */
    public static class Action {
        /**
         * Logable boolean.
         * 执行异常 打印
         * 包含traceId
         *
         * @param ruleName the rule name
         * @return the boolean
         */
        public static boolean errLogabled(String ruleName) {
            return getRule(ruleName).getLogConfig().getErrLogabled();
        }

        /**
         * Cost boolean.
         * 执行链路打印
         * 执行时长
         *
         * @param ruleName the rule name
         * @return the boolean
         */
        public static boolean costLogabled(String ruleName) {
            return getRule(ruleName).getLogConfig().getCostLogabled();
        }
    }

}
