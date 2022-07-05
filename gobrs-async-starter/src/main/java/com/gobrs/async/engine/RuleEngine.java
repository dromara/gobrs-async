package com.gobrs.async.engine;


import com.gobrs.async.rule.Rule;

/**
 * The interface Rule engine.
 *
 * @program: gobrs -async
 * @description: Rules engine
 * @author: sizegang
 */
public interface RuleEngine extends Engine {
    /**
     * 规则解析
     *
     * @param r 待解析rules
     * @return
     */
    void parse(String r);

    /**
     * Do parse.
     *
     * @param r      the r
     * @param reload the reload
     */
    void doParse(Rule r, boolean reload);

}
