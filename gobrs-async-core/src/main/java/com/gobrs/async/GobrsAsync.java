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

    private final TaskBus taskBus;

    private TaskTrigger trigger;

    private volatile boolean ready = false;

    /**
     * Sirector constructor
     *
     * @param executorService the executor service instance used by sirector.
     */
    public GobrsAsync(ExecutorService executorService) {
        this.executorService = executorService;
        this.taskBus = new TaskBus();
    }


    public TaskCluster begin(AsyncTask... tasks) {
        return taskBus.start(tasks);
    }

    public TaskCluster after(AsyncTask... eventHandlers) {
        return taskBus.after(eventHandlers);
    }

    public AsyncResult start(AsyncParam param, long timeout) {
        if (!ready) {
            throw new IllegalStateException("sirector not started.");
        }
        return trigger.build(param, timeout).run();
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
        return trigger.build(param, 0, callback).run();
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
            taskBus.ready();
            trigger = new TaskTrigger(taskBus, executorService);
            ready = true;
        }
    }

}

