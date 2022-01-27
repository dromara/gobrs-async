package com.jd.platform.gobrs.async.engine;

import com.jd.platform.gobrs.async.rule.Rule;
import com.jd.platform.gobrs.async.wrapper.TaskWrapper;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @program: gobrs-async
 * @description: 规则解析引擎
 * @author: sizegang
 **/
public interface Engine {

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
    Map<String, TaskWrapper> parsing(Rule r, Map<String, Object> params);


    /**
     * 参数解析
     */
    Map<String, TaskWrapper> invokeParam(Map<String, TaskWrapper> t, Object v);

    Map<String, TaskWrapper> invokeParamsSupplier(Map<String, TaskWrapper> t, Supplier<Map<String, Object>> supplier);

}
