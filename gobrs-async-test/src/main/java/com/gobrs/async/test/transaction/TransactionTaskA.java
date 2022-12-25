package com.gobrs.async.test.transaction;

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
public class TransactionTaskA extends AsyncTask {

    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println(" TransactionTaskA 任务执行");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("TransactionTaskA 任务执行完成");
        return "TransactionTaskA";
    }

    @Override
    public void rollback(Object o) {
        System.out.println("TransactionTaskA 开始执行回滚");
    }
}
