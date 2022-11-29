package com.gobrs.async.test.task;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;
import org.springframework.stereotype.Component;

/**
 * The type G service.
 *
 * @program: gobrs -async-starter
 * @ClassName EService
 * @description:
 * @author: sizegang
 * @create: 2022 -03-20
 */
@Task(callback = true)
public class GService extends AsyncTask {
    /**
     * The .
     */
    int i  = 10000;
    @Override
    public void prepare(Object o) {
        System.out.println(this.getName() + " 使用线程---" + Thread.currentThread().getName());
    }

    @Override
    public Object task(Object o, TaskSupport support) {
        try {
            System.out.println("GService Begin");
            Thread.sleep(100);
            System.out.println("GService Finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i1 = 0; i1 < i; i1++) {
            i1 += i1;
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
