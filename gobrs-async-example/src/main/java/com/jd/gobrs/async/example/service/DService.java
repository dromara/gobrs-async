package com.jd.gobrs.async.example.service;

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
public class DService implements AsyncTask<String, Map> {

    @Override
    public Map doTask(String parameter, Map<String, TaskWrapper> map) {
        try {
            System.out.println("开始执行D");
//            Object result = map.get("AService").getWorkResult().getResult();
//            System.out.println("Aservice result : " + result.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put("result", "I am is DService");
            return objectObjectHashMap;
        }catch (Exception ex){
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
        if(b){
            System.out.println("DService success");
        }else{
            System.out.println("DService fail");
        }
    }
}
