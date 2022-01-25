package com.jd.platform.gobrs.async.engine;

import com.jd.platform.gobrs.async.rule.Rule;
import com.jd.platform.gobrs.async.wrapper.TaskWrapper;

import java.util.List;
import java.util.Map;

/**
 * @program: gobrs-async
 * @description: 规则解析引擎
 * @author: sizegang
 * @create: 2022-01-26 01:28
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
    List<TaskWrapper> parsing(Rule r);


}
