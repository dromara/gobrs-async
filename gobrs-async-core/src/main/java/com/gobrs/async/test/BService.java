package com.gobrs.async.test;

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
    public Object task(Object o) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("BService");
        return "BService";
    }

    @Override
    public boolean nessary(Object o) {
        return true;
    }
}
