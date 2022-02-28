package com.jd.gobrs.async.engine;

import com.alibaba.fastjson.JSONArray;
import com.jd.gobrs.async.gobrs.GobrsAsyncStore;
import com.jd.gobrs.async.rule.Rule;
import com.jd.gobrs.async.wrapper.TaskWrapper;

import java.util.*;

import static com.jd.gobrs.async.gobrs.GobrsAsyncStore.taskRuleMap;
import static com.jd.gobrs.async.gobrs.GobrsAsyncStore.ruleMap;

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


    @Override
    public Map<String, List<TaskWrapper>> parse(String rule) {
        return Optional.ofNullable(rule).map((ru) -> {
            Map<String, List<TaskWrapper>> listMap = new HashMap<>();
            List<Rule> rules = JSONArray.parseArray(ru, Rule.class);
            for (Rule r : rules) {
                ruleMap.put(r.getName(), r);
                taskRuleMap.put(r.getName(), doParse(r, Collections.emptyMap()));
            }
            return listMap;
        }).orElse(Collections.emptyMap());
    }

}
