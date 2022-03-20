package com.gobrs.async.example.task;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.task.AsyncTask;
import org.springframework.stereotype.Component;

/**
 * @program: gobrs-async-starter
 * @ClassName EService
 * @description:
 * @author: sizegang
 * @create: 2022-03-20
 **/
@Component
public class GService implements AsyncTask<Object, Object> {

    @Override
    public void prepare(Object o) {

    }

    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println("执行GService");
        return null;
    }

    @Override
    public boolean nessary(Object o, TaskSupport support) {
        return false;
    }

    @Override
    public void onSuccess(TaskSupport support) {

    }

    @Override
    public void onFail(TaskSupport support) {

    }
}
