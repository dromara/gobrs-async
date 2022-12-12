package com.gobrs.async.test.task;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * The type A service.
 *
 * @program: gobrs -async-starter
 * @ClassName AService
 * @description:
 * @author: sizegang
 * @create: 2022 -03-20
 */
@Slf4j
@Task
public class AService extends AsyncTask {

    /**
     * The .
     */
    int i = 10000;

    @Override
    public void prepare(Object o) {
        log.info(this.getName() + " 使用线程---" + Thread.currentThread().getName());
    }

    @SneakyThrows
    @Override
    public String task(Object o, TaskSupport support) {

        System.out.println("AService Begin");
        Thread.sleep(300);
        for (int i1 = 0; i1 < i; i1++) {
            i1 += i1;
        }
        System.out.println("AService Finish");
        return "result";
    }

    @Override
    public boolean necessary(Object o, TaskSupport support) {
        return true;
    }


    @Override
    public void onSuccess(TaskSupport support) {

    }

}
