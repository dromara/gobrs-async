package com.gobrs.async.test.task.retry;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: gobrs-async
 * @ClassName GobrsTaskA
 * @description:
 * @author: sizegang
 * @create: 2022-10-31
 **/
@Slf4j
@Task
public class CaseRetryTaskD extends AsyncTask {

    @Override
    public void prepare(Object o) {
        log.info(this.getName() + " 使用线程---" + Thread.currentThread().getName());
    }
    @SneakyThrows
    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println("CaseRetryTaskD Begin");
        Thread.sleep(100);
        System.out.println("CaseRetryTaskD Finish");
        return "CaseRetryTaskD";
    }
}
