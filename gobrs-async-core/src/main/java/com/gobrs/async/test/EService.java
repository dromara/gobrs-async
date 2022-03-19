package com.gobrs.async.test;

import com.gobrs.async.task.AsyncTask;

/**
 * @program: gobrs-async-starter
 * @ClassName EService
 * @description:
 * @author: sizegang
 * @create: 2022-03-20
 **/
public class EService implements AsyncTask<Object, Object> {

    @Override
    public Object task(Object o) {
        System.out.println("EService");
        return "EService";
    }

    @Override
    public boolean nessary(Object o) {
        return true;
    }
}
