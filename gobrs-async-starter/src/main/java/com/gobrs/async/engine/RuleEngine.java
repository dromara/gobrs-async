package com.gobrs.async.engine;


import com.gobrs.async.rule.Rule;

/**
 * @program: gobrs-async
 * @description: Rules engine
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

    void doParse(Rule r, boolean reload);

}
