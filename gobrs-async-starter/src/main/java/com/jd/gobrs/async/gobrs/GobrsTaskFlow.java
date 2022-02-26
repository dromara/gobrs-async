package com.jd.gobrs.async.gobrs;

import com.jd.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.jd.gobrs.async.constant.GobrsAsyncConstant;
import com.jd.gobrs.async.executor.Async;
import com.jd.gobrs.async.result.AsyncResult;
import com.jd.gobrs.async.rule.Rule;
import com.jd.gobrs.async.wrapper.TaskWrapper;
import com.jd.gobrs.async.engine.RuleParseEngine;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author sizegang1
 * @program: gobrs-async
 * @ClassName GobrsTaskFlow
 * @description: 任务编排管道流
 * @author: sizegang
 * @Version 1.0
 * @date 2022-01-27 23:56
 **/
public class GobrsTaskFlow<T> implements GobrsAsync {

    @Resource
    private RuleParseEngine ruleParseEngine;

    @Resource
    private GobrsAsyncProperties gobrsAsyncProperties;

    /**
     * 默认超时时间 使用统一参数
     *
     * @param ruleName
     * @param t
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public AsyncResult taskFlow(String ruleName, T t) throws ExecutionException, InterruptedException {
        return taskFlow(ruleName, t, gobrsAsyncProperties.getTimeout());
    }

    /**
     * 默认超时时间 使用自定义方法参数
     *
     * @param ruleName
     * @param paramSupplier
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public AsyncResult taskFlow(String ruleName, Supplier<Map<String, T>> paramSupplier) throws ExecutionException, InterruptedException {
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
    public AsyncResult taskFlow(String ruleName, T t, long timeout) throws ExecutionException, InterruptedException {
        return ruleParseEngine.exec(ruleName, () -> {
            Map<String, Object> map = new HashMap<>();
            map.put(GobrsAsyncConstant.DEFAULT_PARAMS, t);
            return map;
        }, timeout);
    }

    /**
     * 根据map中存在的key参数进行自定义设置参数
     *
     * @param ruleName
     * @param paramSupplier
     * @return
     */
    public AsyncResult taskFlow(String ruleName, Supplier<Map<String, T>> paramSupplier, long timeout) throws ExecutionException, InterruptedException {
        return ruleParseEngine.exec(ruleName, paramSupplier, timeout);
    }
}
