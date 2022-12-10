package com.gobrs.async.core.engine;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.core.config.GobrsAsyncRule;
import com.gobrs.async.core.holder.BeanHolder;
import com.gobrs.async.core.common.util.JsonUtil;

import java.util.*;

/**
 * The type Abstract com.gobrs.async.engine.
 *
 * @author sizegang1
 * @program: gobrs -async
 * @ClassName AbstractEngine
 * @description:
 * @author: sizegang
 * @Version 1.0
 * @date 2022 -01-27 22:04
 */
public abstract class AbstractEngine implements RuleEngine {


    /**
     * Process com.gobrs.async.rule analysis
     *
     * @param rule
     */
    @Override
    public void parse(String rule) {
        GobrsAsync gobrsAsync = BeanHolder.getBean(GobrsAsync.class);
        List<GobrsAsyncRule> rules = JsonUtil.string2Obj(rule, List.class);
        for (GobrsAsyncRule r : rules) {
            /**
             * true com.gobrs.async.rule enforcer
             */
            doParse(r, false);
            /**
             * Trigger com.gobrs.async.com.gobrs.async.test.task ready to execute
             */
            gobrsAsync.readyTo(r.getName());
        }
    }

}
