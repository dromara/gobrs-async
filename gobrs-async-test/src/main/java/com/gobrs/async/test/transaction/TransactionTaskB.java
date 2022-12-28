package com.gobrs.async.test.transaction;

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
public class TransactionTaskB extends AsyncTask {

    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println(" TransactionTaskB  任务执行");
        return "BResult";
    }


    @Override
    public void rollback(Object o) {
        System.out.println("TransactionTaskB 开始执行回滚");
    }
}
