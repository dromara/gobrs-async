package com.jd.gobrs.async.example.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.gobrs.async.example.executor.SerExector;
import com.jd.gobrs.async.task.AsyncTask;
import com.jd.gobrs.async.task.TaskResult;
import com.jd.gobrs.async.wrapper.TaskWrapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: gobrs-async-example
 * @ClassName CService
 * @description:
 * @author: sizegang
 * @create: 2022-01-29 21:03
 * @Version 1.0
 **/
@Service
public class CService implements AsyncTask<String, Map>, SerExector {


    @Override
    public Map task(String params, Map<String, TaskWrapper> resultSet, Long businessId) {

//        System.out.println("开始执行C");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("result", "我是C的结果");
        return objectObjectHashMap;
    }

    @Override
    public boolean nessary(String aBoolean) {
        return true;
    }

    @Override
    public void result(boolean b, String aBoolean, TaskResult<Map> taskResult) {
        if(b){
//            System.out.println("CService success" + JSONObject.toJSONString(taskResult.getResult().get("result")));
        }else{
            System.out.println("CService fail" );
//            throw new RuntimeException(taskResult.getEx());
        }
    }
}
