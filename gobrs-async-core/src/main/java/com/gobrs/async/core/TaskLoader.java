package com.gobrs.async.core;

import com.gobrs.async.core.callback.AsyncTaskPostInterceptor;
import com.gobrs.async.core.callback.AsyncTaskPreInterceptor;
import com.gobrs.async.core.callback.ErrorCallback;
import com.gobrs.async.core.callback.AsyncTaskExceptionInterceptor;
import com.gobrs.async.core.common.domain.AsyncResult;
import com.gobrs.async.core.common.enums.ExpState;
import com.gobrs.async.core.common.enums.ResultState;
import com.gobrs.async.core.config.ConfigManager;
import com.gobrs.async.core.holder.BeanHolder;
import com.gobrs.async.core.log.LogCreator;
import com.gobrs.async.core.log.LogWrapper;
import com.gobrs.async.core.task.AsyncTask;
import com.gobrs.async.core.common.exception.GobrsAsyncException;
import com.gobrs.async.core.common.exception.TimeoutException;
import lombok.extern.slf4j.Slf4j;
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
 * @param <P> the type parameter
 * @param <R> the type parameter
 * @program: gobrs -async-starter
 * @ClassName
 * @description:
 * @author: sizegang
 * @create: 2022 -03-16
 */
@Slf4j
public class TaskLoader<P, R> {
    /**
     * Interruption code
     */
    private AtomicInteger expCode = new AtomicInteger(ExpState.DEFAULT.getCode());

    /**
     * com.gobrs.async.com.gobrs.async.test.task Loader is Running
     */
    private AtomicBoolean isRunning = new AtomicBoolean(true);

    private final ExecutorService executorService;

    private AsyncTaskExceptionInterceptor<P> asyncExceptionInterceptor = BeanHolder.getBean(AsyncTaskExceptionInterceptor.class);

    private AsyncTaskPreInterceptor<P> asyncTaskPreInterceptor = BeanHolder.getBean(AsyncTaskPreInterceptor.class);

    private AsyncTaskPostInterceptor<P> asyncTaskPostInterceptor = BeanHolder.getBean(AsyncTaskPostInterceptor.class);

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
     * The Assistant com.gobrs.async.com.gobrs.async.test.task.
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

    private LogWrapper logWrapper;

    private final static ArrayList<Future<?>> EmptyFutures = new ArrayList<>(0);

    /**
     * 可选择的任务执行
     * 1、整个任务流程中 假如有 A->B->C
     * <p>
     * 如果我只想拿到 B的执行结果就结束
     * <p>
     * 那么 可以通过设置 选择B要执行，框架会自动选择B所依赖的所有任务执行完成之后 执行B 然后结束 返回结果
     */
    private Set<AsyncTask> optionalTasks;

    private String ruleName;

    /**
     * The Any condition prox.
     */
    public Map<TaskActuator, Boolean> anyConditionProx = new ConcurrentHashMap();

    /**
     * Instantiates a new Task loader.
     *
     * @param ruleName        the rule name
     * @param executorService the executor service
     * @param processMap      the process map
     * @param timeout         the timeout
     */
    TaskLoader(String ruleName, ExecutorService executorService, Map<AsyncTask, TaskActuator> processMap,
               long timeout) {
        this.ruleName = ruleName;
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
        /**
         * 获取任务链初始任务
         */
        AsyncResult result = null;
        try {
            List<TaskActuator> begins = getBeginProcess();

            /**
             * 可选任务
             */
            begins = preOptimal(begins);

            /**
             * 并发开始执行每条任务链
             */
            for (TaskActuator process : begins) {
                /**
                 * Start the thread to perform tasks without any dependencies
                 */
                startProcess(process);
            }

            // wait
            waitIfNecessary();
            back(begins);
        } catch (Exception exception) {
            throw exception;
        } finally {
            return postProcess(result);
        }
    }

    /**
     * 后置处理
     * 开启日志 error 级别
     *
     * @param result
     * @return
     */
    private AsyncResult postProcess(AsyncResult result) {
        if (ConfigManager.Action.costLogabled(ruleName) && log.isErrorEnabled()) {
            String printContent = LogCreator.processLogs(logWrapper);
            log.info(printContent);
        }
        return result;
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

    /**
     * 获取每条任务链的初始节点
     *
     * @return
     */
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
     * Abnormal com.gobrs.async.callback
     *
     * @param errorCallback Exception parameter encapsulation
     */
    public void error(ErrorCallback errorCallback) {
        asyncExceptionInterceptor.exception(errorCallback);
    }

    /**
     * The process is interrupted by a com.gobrs.async.com.gobrs.async.test.task com.gobrs.async.exception
     *
     * @param errorCallback the error com.gobrs.async.callback
     */
    public void errorInterrupted(ErrorCallback errorCallback) {
        this.error = errorCallback.getThrowable();

        cancel();

        completeLatch.countDown();
        /**
         * manual stopAsync  com.gobrs.async.exception  is null
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
     * @param p        com.gobrs.async.com.gobrs.async.test.task parameter
     * @param taskName taskName
     */
    public void preInterceptor(P p, String taskName) {
        asyncTaskPreInterceptor.preProcess(p, taskName);
    }

    /**
     * Mission post-intercept
     *
     * @param param    com.gobrs.async.com.gobrs.async.test.task Result
     * @param taskName taskName
     */
    public void postInterceptor(P param, String taskName) {
        asyncTaskPostInterceptor.postProcess(param, taskName);
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
     * The main process interrupts and waits for the com.gobrs.async.com.gobrs.async.test.task to flow
     * 主线程等待
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
     * @param asyncTask the async com.gobrs.async.com.gobrs.async.test.task
     * @return the process
     */
    TaskActuator getProcess(AsyncTask asyncTask) {
        return processMap.get(asyncTask);
    }

    /**
     * Start process.
     * 开启线程执行任务
     *
     * @param taskActuator the com.gobrs.async.com.gobrs.async.test.task actuator
     */
    void startProcess(TaskActuator taskActuator) {
        if (timeout > 0 || ConfigManager.getRule(ruleName).isTaskInterrupt()) {
            /**
             * If you need to interrupt then you need to save all the com.gobrs.async.com.gobrs.async.test.task threads and you need to manipulate shared variables
             */
            try {
                lock.lock();
                if (!canceled) {
                    Future<?> submit = executorService.submit(taskActuator);
                    /**
                     * 保存返回future 提供中断 能力
                     */
                    futureLists.add(submit);
                    futureMaps.put(taskActuator.task, submit);
                }
            } finally {
                lock.unlock();
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
     * @param task            the com.gobrs.async.com.gobrs.async.test.task
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
     * Get the com.gobrs.async.com.gobrs.async.test.task Bus
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
        asyncResult.setExecuteCode(expCode.get());
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
     * Gets assistant com.gobrs.async.com.gobrs.async.test.task.
     *
     * @return the assistant com.gobrs.async.com.gobrs.async.test.task
     */
    public TaskTrigger.AssistantTask getAssistantTask() {
        return assistantTask;
    }

    /**
     * Sets assistant com.gobrs.async.com.gobrs.async.test.task.
     *
     * @param assistantTask the assistant com.gobrs.async.com.gobrs.async.test.task
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

    /**
     * Gets log wrapper.
     *
     * @return the log wrapper
     */
    public LogWrapper getLogWrapper() {
        return logWrapper;
    }

    /**
     * Sets log wrapper.
     *
     * @param logWrapper the log wrapper
     */
    public void setLogWrapper(LogWrapper logWrapper) {
        this.logWrapper = logWrapper;
    }

    /**
     * Gets rule name.
     *
     * @return the rule name
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * Sets rule name.
     *
     * @param ruleName the rule name
     */
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}
