package com.gobrs.async.test.task.interrupt;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;

/**
 * The type D service.
 *
 * @program: gobrs -async-starter
 * @ClassName DService
 * @description:
 * @author: sizegang
 * @create: 2022 -03-20
 */
@Task
public class InterruptTaskB extends AsyncTask<Object, Object> {

    /**
     * The .
     */
    int i = 10000;

    @Override
    public void prepare(Object o) {

    }

    @Override
    public Object task(Object o, TaskSupport support) {
        try {
            System.out.println("InterruptTaskB Begin");
            Thread.sleep(200);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }
            System.out.println(1 / 0);
            System.out.println("InterruptTaskB Finish");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
