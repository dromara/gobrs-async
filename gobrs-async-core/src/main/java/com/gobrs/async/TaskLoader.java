package com.gobrs.async;

import com.gobrs.async.domain.AsyncParam;
import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.task.AsyncTask;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: gobrs-async-starter
 * @ClassName
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/

class TaskLoader {

    private final AsyncParam param;

    private final ExecutorService executorService;

    private final CountDownLatch completeLatch;

    private final Map<AsyncTask, TaskProcess> processMap;

    private final Callback callback;

    private final long timeout;

    private volatile Throwable error;

    private final Lock lock = new ReentrantLock();

    private volatile boolean canceled = false;

    private final ArrayList<Future<?>> futures;

    private TaskSupport taskSupport;

    private final static ArrayList<Future<?>> EmptyFutures = new ArrayList<>(0);

    TaskLoader(AsyncParam param, ExecutorService executorService,
               Map<AsyncTask, TaskProcess> processMap,
               Callback callback, long timeout, TaskSupport taskSupport) {
        this.param = param;
        this.executorService = executorService;
        this.processMap = processMap;
        this.callback = callback;
        if (callback == null) {
            completeLatch = new CountDownLatch(1);
        } else {
            completeLatch = null;
        }
        this.timeout = timeout;
        this.taskSupport = taskSupport;

        if (this.timeout > 0) {
            futures = new ArrayList<>(1);
        } else {
            futures = EmptyFutures;
        }
    }

    AsyncResult load() {
        ArrayList<TaskProcess> begins = getBeginProcess();
        for (TaskProcess process : begins) {
            startProcess(process);
        }
        // wait
        waitIfNecessary();
        return null;
    }

    private ArrayList<TaskProcess> getBeginProcess() {
        ArrayList<TaskProcess> beginsWith = new ArrayList<TaskProcess>(1);
        for (TaskProcess process : processMap.values()) {
            if (!process.hasUnsatisfiedDependcies()) {
                beginsWith.add(process);
            }
        }
        return beginsWith;
    }

    void markAsCompleted() {
        if (callback == null) {
            completeLatch.countDown();
        } else {
            callback.onSuccess(param);
        }
    }

    void markAsError(Throwable error) {
        if (callback == null) {
            this.error = error;
            completeLatch.countDown();
        } else {
            callback.onError(param, error);
        }
    }

    private void cancel() {
        lock.lock();
        try {
            canceled = true;
            for (Future<?> future : futures) {
                future.cancel(true);
            }
        } finally {
            lock.unlock();
        }

    }

    private void waitIfNecessary() {
        /**
         * Synchronize style, we should block script call thread. when all event
         * handler processes are done, we can wake up the call thread. If any
         * exception is thrown when call event handler process, we should catch
         * the exception, and throw the exception in the script call thread.
         */
        if (callback == null) { //
            try {
                if (timeout > 0) {
                    if (!completeLatch.await(timeout, TimeUnit.MILLISECONDS)) {
                        cancel();
                        throw new TimeoutException();
                    }
                } else {
                    completeLatch.await();
                }

                if (error != null) {
                    throw new GobrsAsyncException(error);
                }
            } catch (InterruptedException e) {
                throw new GobrsAsyncException(e);
            }
        }
    }

    Callback getCallback() {
        return callback;
    }

    TaskProcess getProcess(AsyncTask eventHandler) {
        return processMap.get(eventHandler);
    }

    void startProcess(TaskProcess process) {
        if (timeout > 0) {
            lock.lock();
            try {
                if (!canceled) {
                    futures.add(executorService.submit(process));
                }
            } finally {
                lock.unlock();
            }
        } else {
            executorService.submit(process);
        }
    }

}
