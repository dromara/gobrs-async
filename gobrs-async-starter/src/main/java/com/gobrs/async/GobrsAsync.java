package com.gobrs.async;

import com.gobrs.async.domain.AsyncParam;
import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.spring.GobrsSpring;
import com.gobrs.async.task.AsyncTask;
import com.gobrs.async.threadpool.GobrsAsyncThreadPoolFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @program: gobrs-async-starter
 * @ClassName Sirector
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/
public class GobrsAsync {

    private final ExecutorService executorService = GobrsSpring.getBean(GobrsAsyncThreadPoolFactory.class).getThreadPoolExecutor();

    private Map<String, TaskFlow> taskFlow;

    private Map<String, TaskTrigger> trigger;


    public TaskBuilder begin(String taskName, AsyncTask... tasks) {
        return taskFlow.get(taskName).start(tasks);
    }


    public TaskBuilder begin(String taskName, List<AsyncTask> asyncTasks) {
        return taskFlow.get(taskName).start(asyncTasks);
    }

    public TaskBuilder after(String taskName, AsyncTask... eventHandlers) {
        return taskFlow.get(taskName).after(eventHandlers);
    }

    public AsyncResult go(String taskName, AsyncParam param, long timeout) {
        return trigger.get(taskName).trigger(param, timeout).load();
    }

    public AsyncResult go(String taskName, AsyncParam param) {
        return go(taskName, param, 0L);
    }

    public AsyncResult go(String taskName, AsyncParam param, Callback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback can not be null");
        }
        return trigger.get(taskName).trigger(param, 0, callback).load();
    }


    public synchronized void readyTo(String taskName) {
        TaskTrigger taskTrigger = new TaskTrigger(taskFlow.get(taskName), executorService);
        trigger.put(taskName, taskTrigger);
    }

}

