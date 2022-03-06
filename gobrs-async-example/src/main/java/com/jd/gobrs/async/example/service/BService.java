package com.jd.gobrs.async.example.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.gobrs.async.example.DataContext;
import com.jd.gobrs.async.example.executor.SerExector;
import com.jd.gobrs.async.gobrs.GobrsAsyncSupport;
import com.jd.gobrs.async.task.AsyncTask;
import com.jd.gobrs.async.task.TaskResult;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
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
public class BService implements AsyncTask<DataContext, Map>, SerExector {

    @Override
    public void callback(boolean success, DataContext param, TaskResult<Map> workResult) {
        if (success) {
            System.out.println("BService 成功");
        } else {
            System.out.println("BService 失败");
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
        hashMap.put("result", "我是B的结果");
        return null;
    }

    @Override
    public boolean nessary(DataContext params, GobrsAsyncSupport support) {
        return true;
    }
}
