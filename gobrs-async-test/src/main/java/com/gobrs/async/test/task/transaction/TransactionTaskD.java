package com.gobrs.async.test.task.transaction;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;

/**
 * @program: gobrs-async
 * @ClassName CaseOneTaskD
 * @description:  注意  @Task(callback = true) 需要在可能触发异常的任务上
 * @author: sizegang
 * @create: 2022-10-31
 **/
@Task(callback = true)
public class TransactionTaskD extends AsyncTask {

    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println("TransactionTaskD 任务执行");
        System.out.println(1/0);
        return "DResult";
    }
}
