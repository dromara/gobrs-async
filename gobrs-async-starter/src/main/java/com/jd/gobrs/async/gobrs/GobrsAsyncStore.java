package com.jd.gobrs.async.gobrs;

import com.jd.gobrs.async.rule.Rule;
import com.jd.gobrs.async.wrapper.TaskWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: gobrs-async
 * @ClassName GobrsStore
 * @description:
 * @author: sizegang
 * @create: 2022-02-28 23:27
 * @Version 1.0
 **/
public class GobrsAsyncStore {
    public static  Map<String, Rule> ruleMap = new ConcurrentHashMap<>();

    public static Map<String, Map<String, TaskWrapper>> taskRuleMap = new ConcurrentHashMap<>();

    public static Map<String, List<String>> cacheDepends = new ConcurrentHashMap<>();

}
