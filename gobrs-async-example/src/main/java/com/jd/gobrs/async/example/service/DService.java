package com.jd.gobrs.async.example.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.gobrs.async.example.DataContext;
import com.jd.gobrs.async.example.executor.SerExector;
import com.jd.gobrs.async.gobrs.GobrsAsyncSupport;
import com.jd.gobrs.async.task.AsyncTask;
import com.jd.gobrs.async.task.TaskResult;
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
public class DService implements AsyncTask<DataContext, Map>, SerExector {

    @Override
    public void callback(boolean success, DataContext param, TaskResult<Map> workResult) {
        if (success) {
            System.out.println("DService 成功" + JSONObject.toJSONString(workResult.getResult().get("result")));
        } else {
            System.out.println("DService 失败");
        }
    }

    @Override
    public Map task(DataContext params, GobrsAsyncSupport support) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HashMap hashMap = new HashMap();
        hashMap.put("result", "我是DService的结果");
        return hashMap;
    }

    @Override
    public boolean nessary(DataContext params, GobrsAsyncSupport support) {
        return true;
    }
}
