package com.jd.gobrs.async.engine;

import com.alibaba.fastjson.JSONArray;
import com.jd.gobrs.async.rule.Rule;
import com.jd.gobrs.async.wrapper.TaskWrapper;

import java.util.*;

/**
 * @program: gobrs-async
 * @ClassName AbstractEngine
 * @description:
 * @author: sizegang
 * @Version 1.0
 * @date 2022-01-27 22:04
 * @author sizegang1
 **/
public abstract class AbstractEngine implements RuleEngine {
    public Map<String, Rule> ruleMap = new HashMap<>();

    @Override
    public Map<String, List<TaskWrapper>> parse(String rule) {
        return Optional.ofNullable(rule).map((ru) -> {
            Map<String, List<TaskWrapper>> listMap = new HashMap<>();
            List<Rule> rules = JSONArray.parseArray(ru, Rule.class);
            for (Rule r : rules) {
                ruleMap.put(r.getName(), r);
                doParse(r, Collections.emptyMap());
            }
            return listMap;
        }).orElse(Collections.emptyMap());
    }

}
