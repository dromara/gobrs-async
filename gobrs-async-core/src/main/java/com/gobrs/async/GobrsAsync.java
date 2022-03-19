package com.gobrs.async;

import com.gobrs.async.domain.AsyncParam;
import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.task.AsyncTask;

import java.util.concurrent.ExecutorService;

/**
 * @program: gobrs-async-starter
 * @ClassName Sirector
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/
public class GobrsAsync {

    private final ExecutorService executorService;

    private final TaskFlow taskFlow;

    private TaskTrigger trigger;

    private volatile boolean ready = false;

    /**
     * Sirector constructor
     *
     * @param executorService the executor service instance used by gobrs-async.
     */
    public GobrsAsync(ExecutorService executorService) {
        this.executorService = executorService;
        this.taskFlow = new TaskFlow();
    }


    public TaskBuilder begin(AsyncTask... tasks) {
        return taskFlow.start(tasks);
    }

    public TaskBuilder after(AsyncTask... eventHandlers) {
        return taskFlow.after(eventHandlers);
    }

    public AsyncResult go(AsyncParam param, long timeout) {
        if (!ready) {
            throw new IllegalStateException("gobrs-async not started.");
        }
        return trigger.trigger(param, timeout).load();
    }

    public AsyncResult go(AsyncParam param) {
        return go(param, 0L);
    }

    public AsyncResult start(AsyncParam param, Callback callback) {
        if (!ready) {
            throw new IllegalStateException("gobrs-async not started.");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback can not be null");
        }
        return trigger.trigger(param, 0, callback).load();
    }

    public boolean isReady() {
        return ready;
    }


    public synchronized void readyTo() {
        if (!ready) {
            taskFlow.ready();
            trigger = new TaskTrigger(taskFlow, executorService);
            ready = true;
        }
    }

}

