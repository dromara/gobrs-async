package com.jd.platform.gobrs.async.gobrs;

import com.jd.platform.gobrs.async.engine.RuleParseEngine;
import com.jd.platform.gobrs.async.executor.Async;

import javax.annotation.Resource;

/**
 * @program: gobrs-async
 * @ClassName GobrsTaskFlow
 * @description:
 * @author: sizegang
 * @create: 2022-01-26 02:01
 * @Version 1.0
 **/
public class GobrsTaskFlow<T, R> {

    @Resource
    private RuleParseEngine ruleParseEngine;

    public T startTaskFlow(R r, String ruleName) {
//        Async.beginTaskFlow()
        return null;
    }

}
