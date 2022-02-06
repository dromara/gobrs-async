package com.jd.gobrs.async.example.service;

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
public class CService implements AsyncTask<Boolean, Map> {

    @Override
    public Map doTask(Boolean aBoolean, Map<String, TaskWrapper> map) {
        System.out.println("开始执行C");
//        Object result = map.get("BService").getWorkResult().getResult();
//        System.out.println("B result is" + result.toString());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("result", "I am is CService");
        return objectObjectHashMap;
    }

    @Override
    public boolean nessary(Boolean aBoolean) {
        return true;
    }

    @Override
    public void result(boolean b, Boolean aBoolean, TaskResult<Map> taskResult) {
        if(b){
            System.out.println("CService success");
        }else{
            System.out.println("CService fail");
        }
    }
}
