package com.jd.gobrs.async.engine;

import com.jd.gobrs.async.result.AsyncResult;
import com.jd.gobrs.async.rule.Rule;
import com.jd.gobrs.async.wrapper.TaskWrapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * @program: gobrs-async
 * @description: 规则解析引擎
 * @author: sizegang
 **/
public interface RuleEngine extends Engine {
    /**
     * 规则解析
     *
     * @param r 待解析rules
     * @return
     */
    Map<String, List<TaskWrapper>> parse(String r);

    /**
     * 真正解析的方法
     *
     * @param r
     * @return
     */
    Map<String, TaskWrapper> doParse(Rule r, Map<String, Object> params);

    AsyncResult exec(String ruleName, Supplier<Map<String, Object>> supplier, long timeout) throws ExecutionException, InterruptedException;


}
