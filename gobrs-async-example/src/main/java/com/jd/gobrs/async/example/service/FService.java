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
 * @program: gobrs-async
 * @ClassName FService
 * @description:
 * @author: sizegang
 * @create: 2022-02-26 16:01
 * @Version 1.0
 **/
@Service
public class FService implements AsyncTask<DataContext, Map>, SerExector {
    @Override
    public void callback(boolean success, DataContext param, TaskResult<Map> workResult) {
        if (success) {
            System.out.println("FService 成功" + JSONObject.toJSONString(workResult.getResult().get("result")));
        } else {
            System.out.println("FService 失败");
        }
    }

    @Override
    public Map task(DataContext params, GobrsAsyncSupport support) {
        try {
            Map result = getResult(support, EService.class, Map.class);
            System.out.println("准备拿EService结果" + JSONObject.toJSONString(result.get("result")));
//            DataContext result = getResult(support, EService.class, DataContext.class);
//            System.out.println(JSONObject.toJSONString(result.getResult()));
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HashMap hashMap = new HashMap();
        hashMap.put("result", "我是FService的结果");
        return hashMap;
    }

    @Override
    public boolean nessary(DataContext params, GobrsAsyncSupport support) {
        return true;
    }
}
