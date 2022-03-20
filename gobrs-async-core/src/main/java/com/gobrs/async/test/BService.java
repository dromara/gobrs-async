package com.gobrs.async.test;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.task.AsyncTask;

/**
 * @program: gobrs-async-starter
 * @ClassName BService
 * @description:
 * @author: sizegang
 * @create: 2022-03-20
 **/
public class BService implements AsyncTask<Object, Object> {

    @Override
    public void prepare(Object o) {

    }

    @Override
    public Object task(Object o, TaskSupport support) {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        System.out.println("BService");
        return "BService";
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
