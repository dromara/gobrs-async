package com.jd.platform.gobrs.async.gobrs;

import com.jd.platform.gobrs.async.engine.RuleParseEngine;
import com.jd.platform.gobrs.async.parameter.GobrsAsyncParameter;
import com.jd.platform.gobrs.async.wrapper.TaskWrapper;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @program: gobrs-async
 * @ClassName GobrsTaskFlow
 * @description:
 * @author: sizegang
 * @create: 2022-01-26 02:01
 * @Version 1.0
 **/
public class GobrsTaskFlow<T, R> implements GobrsAsync {

    @Resource
    private RuleParseEngine ruleParseEngine;

    @Resource
    private GobrsAsyncParameter gobrsAsyncParameter;

    public T startTaskFlow(R r, String ruleName) {
        return null;
    }


}
