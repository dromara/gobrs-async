package com.gobrs.async.test;

import com.gobrs.async.task.AsyncTask;

/**
 * @program: gobrs-async-starter
 * @ClassName DService
 * @description:
 * @author: sizegang
 * @create: 2022-03-20
 **/
public class DService implements AsyncTask<Object, Object> {

    @Override
    public Object task(Object o) {

        System.out.println("DService");

        if(true){
            while (true){
                System.out.println(1);
            }
        }

        return "DService";
    }

    @Override
    public boolean nessary(Object o) {
        return true;
    }
}
