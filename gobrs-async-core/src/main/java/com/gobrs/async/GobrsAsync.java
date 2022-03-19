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
     * @param executorService the executor service instance used by sirector.
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

    public AsyncResult start(AsyncParam param, long timeout) {
        if (!ready) {
            throw new IllegalStateException("sirector not started.");
        }
        return trigger.trigger(param, timeout).load();
    }

    public AsyncResult start(AsyncParam param) {
        return start(param, (long) 0);
    }

    public AsyncResult start(AsyncParam param, Callback callback) {
        if (!ready) {
            throw new IllegalStateException("sirector not started.");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback can not be null");
        }
        return trigger.trigger(param, 0, callback).load();
    }

    public boolean isReady() {
        return ready;
    }

    /**
     * Mark the event transaction type of the sirector instance as ready. This
     * method should be called before start any events. And we should not
     * called transaction type building methods any more after this method
     * called. otherwise {@link IllegalStateException} will be throwed.
     *
     * @see GobrsAsync#isReady()
     */
    public synchronized void ready() {
        if (!ready) {
            taskFlow.ready();
            trigger = new TaskTrigger(taskFlow, executorService);
            ready = true;
        }
    }

}

