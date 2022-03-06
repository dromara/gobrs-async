package com.jd.gobrs.async.example.service;

import com.jd.gobrs.async.gobrs.GobrsAsyncSupport;
import com.jd.gobrs.async.task.AsyncTask;
import com.jd.gobrs.async.task.TaskResult;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @program: gobrs-async
 * @ClassName TaskBean
 * @description: 异步任务 任务参数 String类型 任务返回 Map类型
 * @author: sizegang
 * @create: 2022-03-06
 **/
@Service
public class TaskBean implements AsyncTask<String, Map> {

    /**
     *
     * @param success 任务是否执行完成
     * @param param   任务执行参数
     * @param workResult 任务执行结果封装
     */
    @Override
    public void callback(boolean success, String param, TaskResult<Map> workResult) {
        if(success){
            //
            Map result = workResult.getResult();
            //  to send mq result
        }else{
            // to console or  报警
        }
    }

    /**
     * 在这里做耗时操作，如rpc请求、IO等
     * @param params  params 任务参数
     * @param support support 任务流对象
     * @return
     */
    @Override
    public Map task(String params, GobrsAsyncSupport support) {
        return null;
    }

    /**
     * 是否执行当前任务， 根据返回值判断当前任务是否需要执行
     * @param params  params 任务参数
     * @param support support 任务流对象
     * @return
     */
    @Override
    public boolean nessary(String params, GobrsAsyncSupport support) {
        if("cancel".equals(params)){
            return false;
        }
        return true;
    }
}





