package com.jd.gobrs.async.example.service;

import com.jd.gobrs.async.task.AsyncTask;
import com.jd.gobrs.async.task.TaskResult;
import com.jd.gobrs.async.wrapper.TaskWrapper;
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
public class BService implements AsyncTask<Integer, Map> {

    @Override
    public Map doTask(Integer integer, Map<String, TaskWrapper> map) {
        System.out.println("开始执行B");
//        Object result = map.get("AService").getWorkResult().getResult();
//        System.out.println("A result is" + result.toString());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("result", "I am is BService");
        return objectObjectHashMap;
    }

    @Override
    public boolean nessary(Integer integer) {
        return true;
    }

    @Override
    public void result(boolean b, Integer integer, TaskResult<Map> taskResult) {
        if(b){
            System.out.println("BService success");
        }else{
            System.out.println("BService fail");
        }
    }
}
