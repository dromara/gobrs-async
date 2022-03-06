package com.jd.gobrs.async.example.service;

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
public class EService implements AsyncTask<DataContext, DataContext>, ParaExector {

    @Override
    public void callback(boolean success, DataContext param, TaskResult<DataContext> workResult) {
        if (success) {
            System.out.println("EService 成功");
        } else {
            System.out.println("EService 失败");
        }
    }

    @Override
    public DataContext task(DataContext params, GobrsAsyncSupport support) {
        stopTaskFlow(support, 100);
        return params;
    }

    @Override
    public boolean nessary(DataContext params, GobrsAsyncSupport support) {
        return true;
    }
}
