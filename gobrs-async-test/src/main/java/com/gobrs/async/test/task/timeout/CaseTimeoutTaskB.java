package com.gobrs.async.test.task.timeout;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;
import lombok.extern.slf4j.Slf4j;

/**
 * The type B service.
 *
 * @program: gobrs -async-starter
 * @description:
 * @author: sizegang
 * @create: 2022 -03-20
 */
@Slf4j
@Task(failSubExec = true)
public class CaseTimeoutTaskB extends AsyncTask {

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
        System.out.println("CaseTimeoutTaskB Begin");
        for (int i1 = 0; i1 < i; i1++) {
            i1 += i1;
        }
//        System.out.println(1 / 0);
        System.out.println("CaseTimeoutTaskB Finish");
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
