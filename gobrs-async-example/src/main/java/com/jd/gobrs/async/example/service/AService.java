package com.jd.gobrs.async.example.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.gobrs.async.example.DataContext;
import com.jd.gobrs.async.example.executor.ParaExector;
import com.jd.gobrs.async.gobrs.GobrsAsyncSupport;
import com.jd.gobrs.async.task.AsyncTask;
import com.jd.gobrs.async.task.TaskResult;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @program: gobrs-async-example
 * @ClassName AService
 * @description:
 * @author: sizegang
 * @create: 2022-01-29 21:01
 * @Version 1.0
 **/
@Service
public class AService implements AsyncTask<DataContext, Map>, ParaExector {
    @Override
    public void result(boolean success, DataContext param, TaskResult<Map> workResult) {

    }

    @Override
    public Map task(DataContext params, GobrsAsyncSupport support) {
        return null;
    }

    @Override
    public boolean nessary(DataContext params, GobrsAsyncSupport support) {
        return false;
    }
}
