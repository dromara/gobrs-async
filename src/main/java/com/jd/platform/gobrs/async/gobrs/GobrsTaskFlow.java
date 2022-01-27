package com.jd.platform.gobrs.async.gobrs;

import com.jd.platform.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.jd.platform.gobrs.async.engine.RuleParseEngine;
import com.jd.platform.gobrs.async.executor.Async;
import com.jd.platform.gobrs.async.parameter.GobrsAsyncParameter;
import com.jd.platform.gobrs.async.wrapper.TaskWrapper;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * @program: gobrs-async
 * @ClassName GobrsTaskFlow
 * @description:
 * @author: sizegang
 * @create: 2022-01-26 02:01
 * @Version 1.0
 **/
public class GobrsTaskFlow<P, R> implements GobrsAsync {

    @Resource
    private RuleParseEngine ruleParseEngine;

    @Resource
    private GobrsAsyncProperties gobrsAsyncProperties;

    @Resource
    private GobrsAsyncParameter gobrsAsyncParameter;

    public R taskFlow(String ruleName, P p, long timeout) throws ExecutionException, InterruptedException {
        Map<String, TaskWrapper> taskWrapperMap = ruleParseEngine.flatWrapMap.get(ruleName);
        List<TaskWrapper> taskWrapperList = ruleParseEngine.taskWraMap.get(ruleName);

        for (Map.Entry<String, TaskWrapper> taskWrapper : taskWrapperMap.entrySet()) {
            taskWrapper.getValue().setParam(p);
        }
        Async.startTaskFlow(timeout, taskWrapperList);
        return null;
    }


    public R taskFlow(String ruleName, P p) throws ExecutionException, InterruptedException {
        return taskFlow(ruleName, p, gobrsAsyncProperties.getTimeout());
    }

    public R taskFlow(String ruleName, Supplier<Map<String, P>> paramSupplier) {
        Map<String, P> stringPMap = paramSupplier.get();
        return null;
    }
}
