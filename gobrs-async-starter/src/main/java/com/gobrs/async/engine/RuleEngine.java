package com.gobrs.async.engine;

import com.gobrs.async.rule.Rule;

import java.util.Map;

/**
 * @program: gobrs-async
 * @description: 规则解析引擎
 * @author: sizegang
 **/
public interface RuleEngine extends Engine {
    /**
     * 规则解析
     *
     * @param r 待解析rules
     * @return
     */
    void parse(String r);

}
