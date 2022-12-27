package com.gobrs.async.test.task.sence.casefour;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;
import lombok.SneakyThrows;

/**
 * The type Case three task e.
 *
 * @program: gobrs -async
 * @ClassName CaseOneTaskD
 * @description:
 * @author: sizegang
 * @create: 2022 -10-31
 */
@Task
public class CaseFourTaskE extends AsyncTask {

    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println("E开始任务执行");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("E任务执行完成");
        return "EResult";
    }

    @Override
    public void prepare(Object o) {
        System.out.println("CaseFourTaskE " + Thread.currentThread().getName());
    }
}
