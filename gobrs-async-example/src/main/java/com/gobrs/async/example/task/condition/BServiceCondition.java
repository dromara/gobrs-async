package com.gobrs.async.example.task.condition;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.anno.Task;
import com.gobrs.async.task.AsyncTask;
import org.springframework.stereotype.Component;

/**
 * The type B service.
 *
 * @program: gobrs -async-starter
 * @ClassName BService
 * @description:
 * @author: sizegang
 * @create: 2022 -03-20
 */
@Component
@Task
public class BServiceCondition extends AsyncTask<Object, Boolean> {


    /**
     * The .
     */
    int i = 10000;


    @Override
    public void prepare(Object o) {

    }

    @Override
    public Boolean task(Object o, TaskSupport support) {
        System.out.println("BServiceCondition Begin");
        for (int i1 = 0; i1 < i; i1++) {
            i1 += i1;
        }

        System.out.println(1 / 0);
        System.out.println("BServiceCondition Finish");
        return false;
    }

    @Override
    public boolean nessary(Object o, TaskSupport support) {
        return true;
    }

    @Override
    public void onSuccess(TaskSupport support) {

    }

    @Override
    public void onFail(TaskSupport support) {

    }
}
