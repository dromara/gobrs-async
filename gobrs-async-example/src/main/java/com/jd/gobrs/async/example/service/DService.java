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
 * @ClassName DService
 * @description:
 * @author: sizegang
 * @create: 2022-01-29 21:04
 * @Version 1.0
 **/
@Service
public class DService implements AsyncTask<String, Map, Object> {


    @Override
    public Map task(String object, Map<String, TaskWrapper> dataSources, Long businessId) {

        try {
            Object data = getData(dataSources, businessId, AService.class);
            System.out.println(JSONObject.toJSONString(data));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put("result", "我是D的结果");
            return objectObjectHashMap;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean nessary(String s) {
        return true;
    }

    @Override
    public void result(boolean b, String s, TaskResult<Map> taskResult) {
        if (b) {
            System.out.println("DService success" + JSONObject.toJSONString(taskResult.getResult().get("result")));
        } else {
            System.out.println("DService fail");
        }
    }
}
