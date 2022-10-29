package com.gobrs.async.core.config;

import com.gobrs.async.core.rule.Rule;

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
    private Map<String, Rule> processRules = new ConcurrentHashMap();

    /**
     * 全局配置
     */
    private GobrsAsyncProperties gobrsAsyncProperties;

    /**
     * Gets process rules.
     *
     * @return the process rules
     */
    public Map<String, Rule> getProcessRules() {
        return processRules;
    }

    /**
     * Sets process rules.
     *
     * @param processRules the process rules
     */
    public void setProcessRules(Map<String, Rule> processRules) {
        this.processRules = processRules;
    }

    /**
     * Gets gobrs async properties.
     *
     * @return the gobrs async properties
     */
    public GobrsAsyncProperties getGobrsAsyncProperties() {
        return gobrsAsyncProperties;
    }

    /**
     * Sets gobrs async properties.
     *
     * @param gobrsAsyncProperties the gobrs async properties
     */
    public void setGobrsAsyncProperties(GobrsAsyncProperties gobrsAsyncProperties) {
        this.gobrsAsyncProperties = gobrsAsyncProperties;
    }

    /**
     * Instantiates a new Config cache manager.
     *
     * @param gobrsAsyncProperties the gobrs async properties
     */
    public ConfigFactory(GobrsAsyncProperties gobrsAsyncProperties) {
        this.gobrsAsyncProperties = gobrsAsyncProperties;
    }

    /**
     * Add rule rule.
     *
     * @param ruleName the rule name
     * @param rule     the rule
     * @return the rule
     */
    public Rule addRule(String ruleName, Rule rule) {
        return this.processRules.put(ruleName, rule);
    }
}
