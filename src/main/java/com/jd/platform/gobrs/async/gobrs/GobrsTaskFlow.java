package com.jd.platform.gobrs.async.gobrs;

import com.jd.platform.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.jd.platform.gobrs.async.engine.RuleParseEngine;
import com.jd.platform.gobrs.async.executor.Async;
import com.jd.platform.gobrs.async.rule.Rule;
import com.jd.platform.gobrs.async.wrapper.TaskWrapper;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @program: gobrs-async
 * @ClassName GobrsTaskFlow
 * @description:
 * @author: sizegang
 * @Version 1.0
 **/
public class GobrsTaskFlow<T> implements GobrsAsync {

    @Resource
    private RuleParseEngine ruleParseEngine;

    @Resource
    private GobrsAsyncProperties gobrsAsyncProperties;

    public boolean taskFlow(String ruleName, T t) throws ExecutionException, InterruptedException {
        return taskFlow(ruleName, t, gobrsAsyncProperties.getTimeout());
    }

    public boolean taskFlow(String ruleName, Supplier<Map<String, T>> paramSupplier) throws ExecutionException, InterruptedException {
        return taskFlow(ruleName, paramSupplier, gobrsAsyncProperties.getTimeout());
    }

    /**
     * 规则中的task 都是用同一个参数对象  适合数据上下文 dataContext
     *
     * @param ruleName
     * @param t
     * @param timeout
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public boolean taskFlow(String ruleName, T t, long timeout) throws ExecutionException, InterruptedException {
        Rule rule = ruleParseEngine.getRule(ruleName);
        Map<String, Object> map = new HashMap<>();
        map.put(RuleParseEngine.DEFAULT_PARAMS, t);
        Map<String, TaskWrapper> wrapperMap = ruleParseEngine.parsing(rule, map);
        ruleParseEngine.invokeParam(wrapperMap, t);
        return Async.startTaskFlow(timeout, wrapperMap.values().parallelStream().collect(Collectors.toList()));
    }

    /**
     * 根据map中存在的key参数进行自定义设置参数
     *
     * @param ruleName
     * @param paramSupplier
     * @return
     */
    public boolean taskFlow(String ruleName, Supplier<Map<String, T>> paramSupplier, long timeout) throws ExecutionException, InterruptedException {
        Rule rule = ruleParseEngine.getRule(ruleName);
        Map<String, TaskWrapper> wrapperMap = ruleParseEngine.parsing(rule, paramSupplier.get());
        ruleParseEngine.invokeParamsSupplier(wrapperMap, paramSupplier);
        return Async.startTaskFlow(timeout, wrapperMap.values().parallelStream().collect(Collectors.toList()));
    }
}
