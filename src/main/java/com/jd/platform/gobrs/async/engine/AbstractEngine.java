package com.jd.platform.gobrs.async.engine;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jd.platform.gobrs.async.rule.Rule;
import com.jd.platform.gobrs.async.wrapper.TaskWrapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: gobrs-async
 * @ClassName AbstractEngine
 * @description:
 * @author: sizegang
 * @create: 2022-01-26 01:30
 * @Version 1.0
 **/
/*[
  {
    "name": "xxxx"
    "content": "A -> B,C; B->D; C->E; D->F:must; E->F:must;"
  }
]
*/
public abstract class AbstractEngine<T, R> implements Engine {


    public static  Map<String, List<TaskWrapper>> taskWrappers = new ConcurrentHashMap();

    @Override
    public Map<String, List<TaskWrapper>> parse(String rule) {
        return Optional.ofNullable(rule).map((ru) -> {
            Map<String, List<TaskWrapper>> listMap = new HashMap<>();
            JSONArray rules = JSONArray.parseArray(ru);
            for (Object o : rules) {
                Rule r = JSONObject.parseObject(o.toString(), Rule.class);
                List<TaskWrapper> parsing = parsing(r);
                listMap.put(r.getName(), parsing);
            }
            return listMap;
        }).orElse(Collections.emptyMap());
    }









}
