package com.gobrs.async.test.task;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;
import org.springframework.stereotype.Component;

/**
 * The type F service.
 *
 * @program: gobrs -async-starter
 * @ClassName EService
 * @description:
 * @author: sizegang
 * @create: 2022 -03-20
 */
@Task
public class FService extends AsyncTask<Object, Object> {
    /**
     * The .
     */
    int i = 10000;
    @Override
    public void prepare(Object o) {
        System.out.println(this.getName() + " 使用线程---" + Thread.currentThread().getName());
    }

    @Override
    public Object task(Object o, TaskSupport support) {
        try {
            System.out.println("FService Begin");
            Thread.sleep(2000);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }
            System.out.println("FService Finish");
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
