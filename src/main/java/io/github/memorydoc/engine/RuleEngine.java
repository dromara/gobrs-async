package io.github.memorydoc.engine;

import io.github.memorydoc.rule.Rule;
import io.github.memorydoc.wrapper.TaskWrapper;

import java.util.List;
import java.util.Map;

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


}
