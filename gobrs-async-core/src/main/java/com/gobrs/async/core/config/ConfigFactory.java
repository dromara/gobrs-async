package com.gobrs.async.core.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Config cache.
 *
 * @program: gobrs -async
 * @ClassName ConfigCache
 * @description: 配置管理
 * @author: sizegang
 * @create: 2022 -10-28
 */
public class ConfigFactory {

    /**
     * 任务流程独特配置
     */
    private Map<String, GobrsAsyncRule> processRules = new ConcurrentHashMap();

    /**
     * 全局配置
     */
    private GobrsConfig gobrsConfig;

    /**
     * Gets process rules.
     *
     * @return the process rules
     */
    public Map<String, GobrsAsyncRule> getProcessRules() {
        return processRules;
    }

    /**
     * Sets process rules.
     *
     * @param processRules the process rules
     */
    public void setProcessRules(Map<String, GobrsAsyncRule> processRules) {
        this.processRules = processRules;
    }

    /**
     * Gets gobrs async properties.
     *
     * @return the gobrs async properties
     */
    public GobrsConfig getGobrsConfig() {
        return gobrsConfig;
    }

    /**
     * Sets gobrs async properties.
     */
    public void setGobrsAsyncProperties(GobrsConfig gobrsConfig) {
        this.gobrsConfig = gobrsConfig;
    }

    /**
     * Instantiates a new Config cache manager.
     *
     * @param gobrsConfig the gobrs async properties
     */
    public ConfigFactory(GobrsConfig gobrsConfig) {
        this.gobrsConfig = gobrsConfig;
    }

    /**
     * Add rule rule.
     *
     * @param ruleName the rule name
     * @param rule     the rule
     * @return the rule
     */
    public GobrsAsyncRule addRule(String ruleName, GobrsAsyncRule rule) {
        return this.processRules.put(ruleName, rule);
    }
}
