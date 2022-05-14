package com.gobrs.async.engine;

import com.alibaba.fastjson.JSONArray;
import com.gobrs.async.GobrsAsync;
import com.gobrs.async.rule.Rule;
import com.gobrs.async.spring.GobrsSpring;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author sizegang1
 * @program: gobrs-async
 * @ClassName AbstractEngine
 * @description:
 * @author: sizegang
 * @Version 1.0
 * @date 2022-01-27 22:04
 **/
public abstract class AbstractEngine implements RuleEngine {


    /**
     * Process rule analysis
     * @param rule
     */
    @Override
    public void parse(String rule) {
        GobrsAsync gobrsAsync = GobrsSpring.getBean(GobrsAsync.class);
        List<Rule> rules = JSONArray.parseArray(rule, Rule.class);
        for (Rule r : rules) {
            /**
             * true rule enforcer
             */
            doParse(r, false);
            /**
             * Trigger task ready to execute
             */
            gobrsAsync.readyTo(r.getName());
        }
    }

}
