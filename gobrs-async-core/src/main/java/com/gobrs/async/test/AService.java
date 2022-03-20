package com.gobrs.async.test;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.task.AsyncTask;

/**
 * @program: gobrs-async-starter
 * @ClassName AService
 * @description:
 * @author: sizegang
 * @create: 2022-03-20
 **/
public class AService implements AsyncTask<Object, Object> {

    @Override
    public void prepare(Object o) {


    }

    @Override
    public Object task(Object o) {
        System.out.println("AService");

        return "AService";
    }

    @Override
    public boolean nessary(Object o) {
        return true;
    }

    @Override
    public void onSuccess(TaskSupport support) {

    }

    @Override
    public void onFail(TaskSupport support) {

    }
}
