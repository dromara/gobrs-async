package com.gobrs.async.test.task.stopasync;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;
import lombok.SneakyThrows;

/**
 * @program: performance-compare
 * @ClassName TasbC
 * @description:
 * @author: sizegang
 * @create: 2022-12-09
 **/
@Task
public class StopAsyncTaskC extends AsyncTask {

    @SneakyThrows
    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println("使用" + Thread.currentThread().getName());
        System.out.println("StopAsyncTaskC");
        stopAsync(support);
        Thread.sleep(100);
        return "StopAsyncTaskC";
    }
}
