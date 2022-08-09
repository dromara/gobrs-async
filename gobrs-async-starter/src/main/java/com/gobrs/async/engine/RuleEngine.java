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
     * Parse.
     *
     * @param r the r
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
