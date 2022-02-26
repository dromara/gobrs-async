package com.jd.gobrs.async.example.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.gobrs.async.task.AsyncTask;
import com.jd.gobrs.async.task.TaskResult;
import com.jd.gobrs.async.wrapper.TaskWrapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: gobrs-async
 * @ClassName GService
 * @description:
 * @author: sizegang
 * @create: 2022-02-26 16:01
 * @Version 1.0
 **/
@Service
public class GService implements AsyncTask<String, Map, Object> {

    @Override
    public void result(boolean success, String param, TaskResult<Map> workResult) {
        if (success) {
            // 这里taskResult 返回的是 自己的task() 执行结果
            System.out.println("GService success" + JSONObject.toJSONString(workResult.getResult().get("result")));
        } else {
            System.out.println("GService fail");
        }
    }

    @Override
    public Map task(String object, Map<String, TaskWrapper> dataSources, Long businessId) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("result", "我是G的结果");
        return objectObjectHashMap;
    }

    @Override
    public boolean nessary(String s) {
        return true;
    }
}
