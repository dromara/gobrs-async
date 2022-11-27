package com.gobrs.async.test.task;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;
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

@Task(failSubExec = true)
public class BService extends AsyncTask {


    /**
     * The .
     */
    int i = 10000;

    @Override
    public void prepare(Object o) {

    }

    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println("BService Begin");
        for (int i1 = 0; i1 < i; i1++) {
            i1 += i1;
        }
//        System.out.println(1 / 0);
        System.out.println("BService Finish");
        return null;
    }

    @Override
    public boolean nessary(Object o, TaskSupport support) {
        return true;
    }

    @Override
    public void onSuccess(TaskSupport support) {

    }
}
