package com.gobrs.async.test.task.stopasync;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;
import lombok.SneakyThrows;

/**
 * @program: performance-compare
 * @ClassName TaskA
 * @description:
 * @author: sizegang
 * @create: 2022-12-09
 **/
@Task
public class StopAsyncTaskD extends AsyncTask {

    @SneakyThrows
    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println("使用" + Thread.currentThread().getName());
        Thread.sleep(200);
        System.out.println("StopAsyncTaskD");
        return "StopAsyncTaskD";
    }
}
