package com.gobrs.async.test;

import com.gobrs.async.task.AsyncTask;

/**
 * @program: gobrs-async-starter
 * @ClassName CService
 * @description:
 * @author: sizegang
 * @create: 2022-03-20
 **/
public class CService implements AsyncTask<Object, Object> {

    @Override
    public Object task(Object o) {
        System.out.println("CService");
        return "CService";
    }

    @Override
    public boolean nessary(Object o) {
        return true;
    }
}
