package com.jd.gobrs.async.example.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.gobrs.async.task.AsyncTask;
import com.jd.gobrs.async.task.TaskResult;
import com.jd.gobrs.async.wrapper.TaskWrapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: gobrs-async-example
 * @ClassName BService
 * @description:
 * @author: sizegang
 * @create: 2022-01-29 21:03
 * @Version 1.0
 **/
@Service
public class BService implements AsyncTask<String, Map, Object> {

    @Override
    public Map task(String object, Map<String, TaskWrapper> dataSources, Long businessId) {
        //        System.out.println("开始执行A");
        System.out.println(1/0);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("result", "我是B的结果");
        return objectObjectHashMap;
    }

    @Override
    public boolean nessary(String integer) {
        return true;
    }

    @Override
    public void result(boolean b, String integer, TaskResult<Map> taskResult) {
        if (b) {
            System.out.println("BService success" + JSONObject.toJSONString(taskResult.getResult().get("result")));
        } else {
            System.out.println("BService fail");
        }
    }
}
