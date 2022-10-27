package com.gobrs.async;

import com.gobrs.async.callback.AsyncTaskPostInterceptor;
import com.gobrs.async.callback.AsyncTaskPreInterceptor;
import com.gobrs.async.callback.ErrorCallback;
import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.callback.AsyncTaskExceptionInterceptor;
import com.gobrs.async.enums.ExpState;
import com.gobrs.async.enums.ResultState;
import com.gobrs.async.exception.GobrsAsyncException;
import com.gobrs.async.exception.TimeoutException;
import com.gobrs.async.spring.GobrsSpring;
import com.gobrs.async.task.AsyncTask;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The type Task loader.
 *
 * @program: gobrs -async-starter
 * @ClassName
 * @description:
 * @author: sizegang
 * @create: 2022 -03-16
 */
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


    /**
     * The Process map.
     */
    public final Map<AsyncTask, TaskActuator> processMap;

    /**
     * The Affir count.
     */
    public AtomicInteger oplCount = new AtomicInteger(0);

    /**
     * The Assistant task.
     */
    public TaskTrigger.AssistantTask assistantTask;

    private final long timeout;

    private volatile Throwable error;

    private final Lock lock = new ReentrantLock();

    private volatile boolean canceled = false;

    /**
     * The Futures.
     */
    public ArrayList<Future<?>> futureLists;

    /**
     * The Futures async.
     */
    public Map<AsyncTask, Future> futureMaps = new ConcurrentHashMap<>();

    private final static ArrayList<Future<?>> EmptyFutures = new ArrayList<>(0);

    private Set<AsyncTask> optionalTasks;

    /**
     * The Any condition prox.
     */
    public Map<TaskActuator, Boolean> anyConditionProx = new ConcurrentHashMap();

    /**
     * Instantiates a new Task loader.
     *
     * @param executorService the executor service
     * @param processMap      the process map
     * @param timeout         the timeout
     */
    TaskLoader(ExecutorService executorService, Map<AsyncTask, TaskActuator> processMap,
               long timeout) {
        this.executorService = executorService;
        this.processMap = processMap;
        completeLatch = new CountDownLatch(1);
        this.timeout = timeout;

        if (this.timeout > 0) {
            futureLists = new ArrayList<>(1);
        } else {
            futureLists = EmptyFutures;
        }
    }

    /**
     * Load async result.
     *
     * @return the async result
     */
    AsyncResult load() {

        List<TaskActuator> begins = getBeginProcess();

        begins = preOptimal(begins);

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

    /**
     * Determine whether it is the optimal solution route
     *
     * @param begins
     * @return
     */
    private List<TaskActuator> preOptimal(List<TaskActuator> begins) {
        if (!CollectionUtils.isEmpty(optionalTasks)) {
            Optimal.ifOptimal(optionalTasks, processMap, assistantTask);
            Map<String, AsyncTask> optMap = optionalTasks.stream().collect(Collectors.toMap(AsyncTask::getName, Function.identity()));
            begins = begins.stream().filter(x -> optMap.get(x.getTask().getName()) != null).collect(Collectors.toList());
        }
        return begins;
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

    /**
     * Completed.
     */
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
     * @param errorCallback the error callback
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
            for (Future<?> future : futureLists) {
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


    /**
     * Gets process.
     *
     * @param asyncTask the async task
     * @return the process
     */
    TaskActuator getProcess(AsyncTask asyncTask) {
        return processMap.get(asyncTask);
    }

    /**
     * Start process.
     *
     * @param taskActuator the task actuator
     */
    void startProcess(TaskActuator taskActuator) {
        if (timeout > 0 || taskActuator.getGobrsAsyncProperties().isTaskInterrupt()) {
            /**
             * If you need to interrupt then you need to save all the task threads and you need to manipulate shared variables
             */
            try {
//                lock.lock();
                if (!canceled) {
                    Future<?> submit = executorService.submit(taskActuator);
                    futureLists.add(submit);
                    futureMaps.put(taskActuator.task, submit);
                }
            } finally {
//                lock.unlock();
            }
        } else {
            /**
             * Run the command without setting the timeout period
             */
            Future<?> submit = executorService.submit(taskActuator);
            futureMaps.put(taskActuator.task, submit);
        }
    }

    /**
     * Assert affir boolean.
     *
     * @param taskActuator the task actuator
     * @return the boolean
     */
    public boolean assertAffir(TaskActuator taskActuator) {
        if (optionalTasks == null) {
            return true;
        }
        taskActuator.setUpstreamDepdends(optionalTasks.size());
        boolean contains = optionalTasks.contains(taskActuator.getTask());
        if (contains) {
            return true;
        }
        return false;
    }

    /**
     * End of single mission line
     * 结束单条任务链
     *
     * @param subtasks the subtasks
     */
    public void stopSingleTaskLine(List<AsyncTask> subtasks) {
        TaskActuator taskActuator = processMap.get(assistantTask);
        for (AsyncTask subtask : subtasks) {
            rtDept(subtask, taskActuator);
        }
    }


    /**
     * Rt dept.
     *
     * @param task            the task
     * @param terminationTask the terminationTask
     */
    public void rtDept(AsyncTask task, TaskActuator terminationTask) {
        if (task instanceof TaskTrigger.AssistantTask) {
            terminationTask.releasingDependency();
            if (!terminationTask.hasUnsatisfiedDependcies()) {
                terminationTask.run();
            }
            return;
        }
        stopSingleTaskLine(processMap.get(task).subTasks);
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
        asyncResult.setSuccess(support.getResultMap().values().stream().allMatch(r -> r.getResultState().equals(ResultState.SUCCESS)));
        return asyncResult;
    }

    /**
     * Gets exp code.
     *
     * @return the exp code
     */
    public AtomicInteger getExpCode() {
        return expCode;
    }

    /**
     * Sets exp code.
     *
     * @param expCode the exp code
     */
    public void setExpCode(AtomicInteger expCode) {
        this.expCode = expCode;
    }

    /**
     * Is running atomic boolean.
     *
     * @return the atomic boolean
     */
    public AtomicBoolean isRunning() {
        return isRunning;
    }

    /**
     * Sets is running.
     *
     * @param isRunning the is running
     */
    public void setIsRunning(boolean isRunning) {
        this.isRunning = new AtomicBoolean(isRunning);
    }

    /**
     * Gets assistant task.
     *
     * @return the assistant task
     */
    public TaskTrigger.AssistantTask getAssistantTask() {
        return assistantTask;
    }

    /**
     * Sets assistant task.
     *
     * @param assistantTask the assistant task
     */
    public void setAssistantTask(TaskTrigger.AssistantTask assistantTask) {
        this.assistantTask = assistantTask;
    }

    /**
     * Gets optional tasks.
     *
     * @return the optional tasks
     */
    public Set<AsyncTask> getOptionalTasks() {
        return optionalTasks;
    }

    /**
     * Sets optional tasks.
     *
     * @param optionalTasks the optional tasks
     */
    public void setOptionalTasks(Set<AsyncTask> optionalTasks) {
        this.optionalTasks = optionalTasks;
    }
}
