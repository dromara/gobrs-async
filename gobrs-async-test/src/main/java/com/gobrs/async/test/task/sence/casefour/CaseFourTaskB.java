package com.gobrs.async.test.task.sence.casefour;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;

/**
 * @program: gobrs-async
 * @ClassName GobrsTaskB
 * @description:
 * @author: sizegang
 * @create: 2022-10-31
 **/
@Task
public class CaseFourTaskB extends AsyncTask {

    @Override
    public Object task(Object o, TaskSupport support) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("B任务执行");
        return "BResult";
    }

    @Override
    public void prepare(Object o) {
        System.out.println("CaseFourTaskB " + Thread.currentThread().getName());
    }
}
