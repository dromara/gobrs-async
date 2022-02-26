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
 * @ClassName AService
 * @description:
 * @author: sizegang
 * @create: 2022-01-29 21:01
 * @Version 1.0
 **/
@Service
public class AService implements AsyncTask<String, Map, Object> {


    @Override
    public Map task(String object, Map<String, TaskWrapper> dataSources, Long businessId) {
        //        System.out.println("开始执行A");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("result", "我是A的结果");
        return objectObjectHashMap;
    }

    @Override
    public boolean nessary(String s) {
        return true;
    }

    @Override
    public void result(boolean b, String s, TaskResult<Map> taskResult) {
        if (b) {
            // 这里taskResult 返回的是 自己的task() 执行结果
            System.out.println("AService success" + JSONObject.toJSONString(taskResult.getResult().get("result")));
        } else {
            System.out.println("AService fail");
        }
    }
}
