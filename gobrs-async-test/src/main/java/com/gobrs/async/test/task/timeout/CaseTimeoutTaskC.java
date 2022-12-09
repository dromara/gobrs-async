package com.gobrs.async.test.task.timeout;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;
import lombok.extern.slf4j.Slf4j;

/**
 * The type F service.
 *
 * @program: gobrs -async-starter
 * @description:
 * @author: sizegang
 * @create: 2022 -03-20
 */
@Slf4j
@Task
public class CaseTimeoutTaskC extends AsyncTask<Object, Object> {
    /**
     * The .
     */
    int i = 10000;

    @Override
    public void prepare(Object o) {
        log.info(this.getName() + " 使用线程---" + Thread.currentThread().getName());
    }

    @Override
    public Object task(Object o, TaskSupport support) {
        try {
            System.out.println("CaseTimeoutTaskC Begin");
            Thread.sleep(2000);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }
            System.out.println("CaseTimeoutTaskC Finish");
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
