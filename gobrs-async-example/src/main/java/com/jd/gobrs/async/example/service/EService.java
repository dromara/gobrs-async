package com.jd.gobrs.async.example.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.gobrs.async.example.DataContext;
import com.jd.gobrs.async.example.executor.ParaExector;
import com.jd.gobrs.async.gobrs.GobrsAsyncSupport;
import com.jd.gobrs.async.task.AsyncTask;
import com.jd.gobrs.async.task.TaskResult;
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
public class EService implements AsyncTask<DataContext, Map>, ParaExector {

    @Override
    public void callback(boolean success, DataContext param, TaskResult<Map> workResult) {
        if (success) {
            System.out.println("EService 成功" + JSONObject.toJSONString(workResult.getResult().get("result")));
        } else {
            System.out.println("EService 失败");
        }
    }

    @Override
    public Map task(DataContext params, GobrsAsyncSupport support) {
        try {
//            System.out.println(1/0);
            Thread.sleep(500);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        byte[] result = new byte[1024 * 1024];
        Map h = new HashMap();
        h.put("result", "我是EService的结果");
        return h;
    }

    @Override
    public boolean nessary(DataContext params, GobrsAsyncSupport support) {
        return true;
    }
}
