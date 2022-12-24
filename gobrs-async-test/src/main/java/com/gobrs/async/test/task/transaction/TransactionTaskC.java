package com.gobrs.async.test.task.transaction;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;

/**
 * @program: gobrs-async
 * @ClassName CaseOneTaskC
 * @description:
 * @author: sizegang
 * @create: 2022-10-31
 **/
@Task
public class TransactionTaskC extends AsyncTask {

    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println(" TransactionTaskC 任务执行");
        return "CResult";
    }


    @Override
    public void rollback(Object o) {
        System.out.println("TransactionTaskC 开始执行回滚");
    }
}
