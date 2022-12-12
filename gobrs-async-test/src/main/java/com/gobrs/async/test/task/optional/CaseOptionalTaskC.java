package com.gobrs.async.test.task.optional;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;

/**
 * The type Case optional task a.
 *
 * @program: gobrs -async
 * @ClassName GobrsTaskA
 * @description:
 * @author: sizegang
 * @create: 2022 -10-31
 */
@Task
public class CaseOptionalTaskC extends AsyncTask<String, Object> {


    @Override
    public Object task(String s, TaskSupport support) {
        System.out.println("CaseOptionalTaskC 任务执行");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("CaseOptionalTaskC 任务执行完成");
        return "CaseOptionalTaskC";
    }
}
