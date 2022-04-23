package com.gobrs.async;

import com.gobrs.async.callback.AsyncTaskPostInterceptor;
import com.gobrs.async.callback.AsyncTaskPreInterceptor;
import com.gobrs.async.callback.ErrorCallback;
import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.callback.AsyncTaskExceptionInterceptor;
import com.gobrs.async.enums.ExpState;
import com.gobrs.async.exception.GobrsAsyncException;
import com.gobrs.async.exception.TimeoutException;
import com.gobrs.async.spring.GobrsSpring;
import com.gobrs.async.task.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private AtomicInteger expCode = new AtomicInteger(ExpState.DEFAULT.getCode());

    /**
     * task Loader is Running
     */
    private AtomicBoolean isRunning = new AtomicBoolean(true);

    private final ExecutorService executorService;

    private AsyncTaskExceptionInterceptor asyncExceptionInterceptor = GobrsSpring.getBean(AsyncTaskExceptionInterceptor.class);

    private AsyncTaskPreInterceptor asyncTaskPreInterceptor = GobrsSpring.getBean(AsyncTaskPreInterceptor.class);

    private AsyncTaskPostInterceptor asyncTaskPostInterceptor = GobrsSpring.getBean(AsyncTaskPostInterceptor.class);

    private final CountDownLatch completeLatch;

    private final Map<AsyncTask, TaskActuator> processMap;

    public TaskTrigger.AssistantTask assistantTask;

    private final long timeout;

    private volatile Throwable error;

    private final Lock lock = new ReentrantLock();

    private volatile boolean canceled = false;

    private final ArrayList<Future<?>> futures;


    private final static ArrayList<Future<?>> EmptyFutures = new ArrayList<>(0);

    TaskLoader(ExecutorService executorService, Map<AsyncTask, TaskActuator> processMap,
               long timeout) {
        this.executorService = executorService;
        this.processMap = processMap;
        completeLatch = new CountDownLatch(1);
        this.timeout = timeout;

        if (this.timeout > 0) {
            futures = new ArrayList<>(1);
        } else {
            futures = EmptyFutures;
        }
    }

    AsyncResult load() {
        ArrayList<TaskActuator> begins = getBeginProcess();
        for (TaskActuator process : begins) {
            /**
             * Start the thread to perform tasks without any dependencies
             */
            startProcess(process);
        }
        // wait
        waitIfNecessary();
        return back(begins);
    }

    private ArrayList<TaskActuator> getBeginProcess() {
        ArrayList<TaskActuator> beginsWith = new ArrayList<>(1);
        for (TaskActuator process : processMap.values()) {
            if (!process.hasUnsatisfiedDependcies()) {
                beginsWith.add(process);
            }
        }
        return beginsWith;
    }

    void completed() {
        completeLatch.countDown();
    }

    /**
     * Abnormal callback
     *
     * @param errorCallback Exception parameter encapsulation
     */
    public void error(ErrorCallback errorCallback) {
        asyncExceptionInterceptor.exception(errorCallback);
    }

    /**
     * The process is interrupted by a task exception
     *
     * @param errorCallback
     */
    public void errorInterrupted(ErrorCallback errorCallback) {
        this.error = errorCallback.getThrowable();

        cancel();

        completeLatch.countDown();
        /**
         * manual stopAsync  exception  is null
         */
        if (errorCallback.getThrowable() != null) {
            /**
             * Global interception listening
             */
            asyncExceptionInterceptor.exception(errorCallback);
        }
    }

    /**
     * Premission interceptor
     *
     * @param object   task parameter
     * @param taskName taskName
     */
    public void preInterceptor(Object object, String taskName) {
        asyncTaskPreInterceptor.preProcess(object, taskName);
    }

    /**
     * Mission post-intercept
     *
     * @param object   task Result
     * @param taskName taskName
     */
    public void postInterceptor(Object object, String taskName) {
        asyncTaskPostInterceptor.postProcess(object, taskName);
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

    /**
     * The main process interrupts and waits for the task to flow
     */
    private void waitIfNecessary() {
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


    TaskActuator getProcess(AsyncTask asyncTask) {
        return processMap.get(asyncTask);
    }

    void startProcess(TaskActuator taskActuator) {

        if (timeout > 0 || taskActuator.getGobrsAsyncProperties().isTaskInterrupt()) {
            /**
             * If you need to interrupt then you need to save all the task threads and you need to manipulate shared variables
             */
            lock.lock();
            try {
                if (!canceled) {
                    futures.add(executorService.submit(taskActuator));
                }
            } finally {
                lock.unlock();
            }
        } else {
            /**
             * Run the command without setting the timeout period
             */
            executorService.submit(taskActuator);
        }
    }

    /**
     * End of single mission line
     *
     * @param taskLines
     */
    public void stopSingleTaskLine(Integer taskLines) {
        TaskActuator taskActuator = processMap.get(assistantTask);
        for (Integer i = 0; i < taskLines; i++) {
            processMap.get(assistantTask).releasingDependency();
        }
        if (!taskActuator.hasUnsatisfiedDependcies()) {
            taskActuator.run();
        }
    }

    /**
     * Get the task Bus
     *
     * @param begins Collection of subtask processes
     * @return
     */
    private TaskSupport getSupport(List<TaskActuator> begins) {
        return begins.get(0).getTaskSupport();
    }

    /**
     * Encapsulate return parameter
     *
     * @param begins
     * @return
     */
    private AsyncResult back(List<TaskActuator> begins) {
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

    public AtomicBoolean isRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = new AtomicBoolean(isRunning);
    }

    public TaskTrigger.AssistantTask getAssistantTask() {
        return assistantTask;
    }

    public void setAssistantTask(TaskTrigger.AssistantTask assistantTask) {
        this.assistantTask = assistantTask;
    }
}
