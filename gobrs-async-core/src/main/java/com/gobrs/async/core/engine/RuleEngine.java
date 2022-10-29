package com.gobrs.async.core.engine;


import com.gobrs.async.core.rule.Rule;

/**
 * The interface Rule com.gobrs.async.engine.
 *
 * @program: gobrs -async
 * @description: Rules com.gobrs.async.engine
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
