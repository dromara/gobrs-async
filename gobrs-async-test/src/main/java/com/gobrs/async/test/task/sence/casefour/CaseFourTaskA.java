package com.gobrs.async.test.task.sence.casefour;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;

/**
 * @program: gobrs-async
 * @ClassName GobrsTaskA
 * @description:
 * @author: sizegang
 * @create: 2022-10-31
 **/
@Task
public class CaseFourTaskA extends AsyncTask {

    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println("A任务执行");
        System.out.println(1/0);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("A任务执行完成");
        return "AResult";
    }
}
