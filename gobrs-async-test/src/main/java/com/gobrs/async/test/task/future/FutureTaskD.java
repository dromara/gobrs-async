package com.gobrs.async.test.task.future;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;
import lombok.SneakyThrows;

/**
 * The type B service.
 *
 * @program: gobrs -async-starter
 * @ClassName BService
 * @description:
 * @author: sizegang
 * @create: 2022 -03-20
 */
@Task
public class FutureTaskD extends AsyncTask {


    /**
     * The .
     */
    int i = 10000;

    @Override
    public void prepare(Object o) {

    }

    @SneakyThrows
    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println("FutureTaskD Begin");
        for (int i1 = 0; i1 < i; i1++) {
            i1 += i1;
        }
        Thread.sleep(20000);
        System.out.println("FutureTaskD Finish");
        return null;
    }

    @Override
    public boolean necessary(Object o, TaskSupport support) {
        return true;
    }

    @Override
    public void onSuccess(TaskSupport support) {

    }
}
