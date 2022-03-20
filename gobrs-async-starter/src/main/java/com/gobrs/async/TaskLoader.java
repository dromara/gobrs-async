package com.gobrs.async;

import com.gobrs.async.callback.ErrorCallback;
import com.gobrs.async.domain.AsyncParam;
import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.exception.AsyncExceptionInterceptor;
import com.gobrs.async.spring.GobrsSpring;
import com.gobrs.async.task.AsyncTask;
import com.gobrs.async.threadpool.GobrsAsyncThreadPoolFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: gobrs-async-starter
 * @ClassName
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/

public class TaskLoader {
    /**
     * Interruption code
     */
    private AtomicInteger expCode;
    private final AsyncParam param;

    private final ExecutorService executorService;

    private AsyncExceptionInterceptor asyncExceptionInterceptor = GobrsSpring.getBean(AsyncExceptionInterceptor.class);

    private final CountDownLatch completeLatch;

    private final Map<AsyncTask, TaskProcess> processMap;

    private final Callback callback;

    private final long timeout;

    private volatile Throwable error;

    private final Lock lock = new ReentrantLock();

    private volatile boolean canceled = false;

    private final ArrayList<Future<?>> futures;


    private final static ArrayList<Future<?>> EmptyFutures = new ArrayList<>(0);

    TaskLoader(AsyncParam param, ExecutorService executorService, Map<AsyncTask, TaskProcess> processMap,
               Callback callback, long timeout) {
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

        if (this.timeout > 0) {
            futures = new ArrayList<>(1);
        } else {
            futures = EmptyFutures;
        }
    }

    AsyncResult load() {
        ArrayList<TaskProcess> begins = getBeginProcess();


        for (TaskProcess process : begins) {
            /**
             * Start the thread to perform tasks without any dependencies
             */
            startProcess(process);
        }
        // wait
        waitIfNecessary();
        return back(begins);
    }

    private ArrayList<TaskProcess> getBeginProcess() {
        ArrayList<TaskProcess> beginsWith = new ArrayList<>(1);
        for (TaskProcess process : processMap.values()) {
            if (!process.hasUnsatisfiedDependcies()) {
                beginsWith.add(process);
            }
        }
        return beginsWith;
    }

    void completed() {
        if (callback == null) {
            completeLatch.countDown();
        } else {
            callback.onSuccess(param);
        }
    }

    public void error(ErrorCallback errorCallback) {
        if (callback != null) {
            callback.onError(errorCallback);
        }
    }

    public void errorInterrupted(ErrorCallback errorCallback) {
        if (callback != null) {
            callback.onError(errorCallback);
        }
        this.error = errorCallback.getThrowable();
        cancel();

        completeLatch.countDown();
        /**
         * Global interception listening
         */
        asyncExceptionInterceptor.exception(errorCallback);


    }

    private void cancel() {
        lock.lock();
        try {
            canceled = true;
            for (Future<?> future : futures) {
                /**
                 * Enforced interruptions
                 */
                future.cancel(true);
            }
        } finally {
            lock.unlock();
        }

    }

    private void waitIfNecessary() {
        if (callback == null) {
            try {
                if (timeout > 0) {
                    if (!completeLatch.await(timeout, TimeUnit.MILLISECONDS)) {
                        cancel();
                        throw new TimeoutException();
                    }
                } else {
                    completeLatch.await(10, TimeUnit.SECONDS);
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

        /**
         * Check whether the command needs to be executed
         */
        if (!process.task.nessary(process.param, process.getTaskSupport())) {
            return;
        }
        /**
         * Don't do it if you've already done it
         */
        Object result = process.support.getResultMap().get(process.task.getClass());
        if (result != null) {
            return;
        }

        if (timeout > 0 || process.getGobrsAsyncProperties().isTaskInterrupt()) {
            /**
             * Only threads in a lock can be interrupted
             */
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


    private TaskSupport getSupport(List<TaskProcess> begins) {
        return begins.get(0).getTaskSupport();
    }


    private AsyncResult back(List<TaskProcess> begins) {
        TaskSupport support = getSupport(begins);
        AsyncResult asyncResult = new AsyncResult();
        asyncResult.setResultMap(support.getResultMap());
        asyncResult.setExpCode(expCode.get());
        asyncResult.setSuccess(true);
        return asyncResult;
    }

    public AtomicInteger getExpCode() {
        return expCode;
    }

    public void setExpCode(AtomicInteger expCode) {
        this.expCode = expCode;
    }
}
