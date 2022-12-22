package com.gobrs.async.core;

import com.gobrs.async.core.callback.AsyncTaskPostInterceptor;
import com.gobrs.async.core.callback.AsyncTaskPreInterceptor;
import com.gobrs.async.core.callback.ErrorCallback;
import com.gobrs.async.core.callback.AsyncTaskExceptionInterceptor;
import com.gobrs.async.core.common.def.DefaultConfig;
import com.gobrs.async.core.common.domain.AsyncResult;
import com.gobrs.async.core.common.enums.ExpState;
import com.gobrs.async.core.common.enums.ResultState;
import com.gobrs.async.core.config.ConfigManager;
import com.gobrs.async.core.config.GobrsAsyncRule;
import com.gobrs.async.core.holder.BeanHolder;
import com.gobrs.async.core.log.LogCreator;
import com.gobrs.async.core.log.LogWrapper;
import com.gobrs.async.core.task.AsyncTask;
import com.gobrs.async.core.common.exception.GobrsAsyncException;
import com.gobrs.async.core.common.exception.AsyncTaskTimeoutException;
import com.gobrs.async.core.timer.GobrsFutureTask;
import com.gobrs.async.core.timer.GobrsTimer;
import com.gobrs.async.plugin.base.wrapper.ThreadWapper;
import com.gobrs.async.spi.ExtensionLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.ref.Reference;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.gobrs.async.core.common.def.DefaultConfig.*;
import static com.gobrs.async.core.common.enums.InterruptEnum.*;
import static com.gobrs.async.core.common.util.ExceptionUtil.excludeInterceptException;
import static com.gobrs.async.core.task.ReUsing.reusing;

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
    private AtomicInteger expCode = new AtomicInteger(ExpState.SUCCESS.getCode());
    private Integer cusCode;

    /**
     * task Loader is Running
     */
    private AtomicBoolean isRunning = new AtomicBoolean(true);

    private final ExecutorService executorService;

    private final AsyncTaskExceptionInterceptor<P> asyncExceptionInterceptor = BeanHolder.getBean(AsyncTaskExceptionInterceptor.class);

    private final AsyncTaskPreInterceptor<P> asyncTaskPreInterceptor = BeanHolder.getBean(AsyncTaskPreInterceptor.class);

    private final AsyncTaskPostInterceptor<P> asyncTaskPostInterceptor = BeanHolder.getBean(AsyncTaskPostInterceptor.class);

    private final CountDownLatch completeLatch;

    /**
     * The Process map.
     */
    public final Map<AsyncTask, TaskActuator> processMap;

    /**
     * The Affir count.
     */
    public final AtomicInteger oplCount = new AtomicInteger(0);

    /**
     * The Assistant task.
     */
    public TaskTrigger.AssistantTask assistantTask;

    private final long processTimeout;

    private volatile Throwable error;

    private static final Lock taskLock = new ReentrantLock();

    private volatile boolean canceled = false;

    /**
     * The constant INTERRUPTFLAG.
     */
    public volatile AtomicInteger INTERRUPTFLAG = new AtomicInteger(INIT.getState());

    /**
     * The Futures.
     */
    public ArrayList<Future<?>> futureTasksLists;

    /**
     * The Futures async.
     */
    public final Map<AsyncTask<P, R>, Future<?>> futureTasksMap = new ConcurrentHashMap<>();

    /**
     * The Timer listeners.
     */
    public final Map<Class<?>, Reference<GobrsTimer.TimerListener>> timerListeners = new ConcurrentHashMap<>();

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
        this.processTimeout = timeout;
        if (this.processTimeout > 0) {
            futureTasksLists = new ArrayList<>(1);
        } else {
            futureTasksLists = EmptyFutures;
        }
    }

    /**
     * Load async result.
     *
     * @return the async result
     * @throws Exception the exception
     */
    AsyncResult load() throws Exception {
        /**
         * 获取任务链初始任务
         */
        AsyncResult result;
        List<TaskActuator> begins = getBeginProcess();
        try {

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
                 * Thread reuse
                 */
                if (begins.size() == 1 && reusing(process)) {
                    process.call();
                } else {
                    startProcess(process);
                }
            }
            // wait
            waitIfNecessary();
            result = back(begins);
            return postProcess(result);
        } catch (Exception exception) {
            if (excludeInterceptException(exception)) {
                throw exception;
            }
        }
        return null;
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
        if (!excludeInterceptException(errorCallback.getThrowable())) {
            return;
        }
        asyncExceptionInterceptor.exception(errorCallback);
    }

    /**
     * The process is interrupted by a task com.gobrs.async.exception
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
            if (!excludeInterceptException(errorCallback.getThrowable())) {
                return;
            }
            asyncExceptionInterceptor.exception(errorCallback);
        }
    }

    /**
     * Premission interceptor
     *
     * @param p        task parameter
     * @param taskName taskName
     */
    public void preInterceptor(P p, String taskName) {
        asyncTaskPreInterceptor.preProcess(p, taskName);
    }

    /**
     * Mission post-intercept
     *
     * @param param    task Result
     * @param taskName taskName
     */
    public void postInterceptor(P param, String taskName) {
        asyncTaskPostInterceptor.postProcess(param, taskName);
    }

    private void cancel() {
        taskLock.lock();
        try {
            canceled = true;
            for (Future<?> future : futureTasksLists) {
                /**
                 * Enforced interruptions
                 */
                future.cancel(true);
            }
        } finally {
            taskLock.unlock();
        }

    }

    /**
     * The main process interrupts and waits for the task to flow
     * 主线程等待
     */
    private void waitIfNecessary() {
        try {
            if (processTimeout > 0) {
                if (!completeLatch.await(processTimeout, TimeUnit.MILLISECONDS)) {
                    cancel();
                    throw new AsyncTaskTimeoutException();
                }
            } else {
                completeLatch.await();
            }
            GobrsAsyncRule rule = ConfigManager.getRule(ruleName);
            if (error != null && rule.isCatchable()) {
                throw new GobrsAsyncException(error);
            }
        } catch (InterruptedException e) {
            throw new GobrsAsyncException(e);
        } finally {
            release();
        }
    }

    private void release() {
        futureTasksMap.clear();
        futureTasksLists.clear();
        timerListeners.clear();
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
     * 开启线程执行任务
     *
     * @param taskActuator the task actuator
     */
    void startProcess(TaskActuator taskActuator) {
        if (processTimeout > 0 || ConfigManager.getRule(ruleName).isTaskInterrupt()) {
            /**
             * If you need to interrupt then you need to save all the task threads and you need to manipulate shared variables
             */
            try {
                taskLock.lock();
                if (!canceled) {
                    Future<?> submit = taskListenerConditional(taskActuator);
                    futureTasksLists.add(submit);
                }

            } finally {
                taskLock.unlock();
            }
        } else {
            /**
             * Run the command without setting the timeout period
             */
            taskListenerConditional(taskActuator);
        }
    }

    private Future<?> taskListenerConditional(TaskActuator taskActuator) {
        if (taskActuator.task.getTimeoutInMilliseconds() > DefaultConfig.TASK_TIME_OUT) {
            return timeOperator(taskActuator);
        }
        Future<?> future = start(taskActuator);
        return future;
    }

    /**
     * https://async.sizegang.cn/pages/2f8gmn/
     *
     * @param taskActuator
     * @return
     */
    private Future<?> timeOperator(TaskActuator<?> taskActuator) {
        Callable<?> callable = threadAdapterSPI(taskActuator);
        GobrsFutureTask<?> future = new GobrsFutureTask<>(callable);
        executorService.submit(future);
        GobrsTimer.TimerListener listener = new GobrsTimer.TimerListener() {
            @Override
            public void tick() {
                try {
                    doTick();
                } catch (Exception exception) {
                    future.cancel(true);
                }
            }

            /**
             * adjust interrupt
             * @return
             */
            private void doTick() {
                boolean b = !future.isDone() && taskActuator.taskStatus().compareAndSet(TASK_INITIALIZE, TASK_TIMEOUT);
                if (b) {
                    try {
                        future.get(0, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        future.isMayStopIfRunning(true);
                    }
                }
            }

            @Override
            public int getIntervalTimeInMilliseconds() {
                return taskActuator.task.getTimeoutInMilliseconds();
            }
        };

        Reference<GobrsTimer.TimerListener> tl = GobrsTimer.getInstance(ConfigManager.getGlobalConfig().getTimeoutCoreSize()).addTimerListener(listener);
        timerListeners.put(taskActuator.getTask().getClass(), tl);
        futureTasksMap.put(taskActuator.task, future);
        return future;
    }

    private Future<?> start(TaskActuator taskActuator) {
        Callable<?> callable = threadAdapterSPI(taskActuator);
        Future<?> future = executorService.submit(callable);
        if (taskActuator.task.isExclusive()) {
            futureTasksMap.put(taskActuator.task, future);
        }
        return future;
    }

    /**
     * 线程 适配 SPI
     *
     * @param taskActuator
     * @return
     */
    private Callable<?> threadAdapterSPI(TaskActuator<?> taskActuator) {
        ThreadWapper threadWapper = ExtensionLoader.getExtensionLoader(ThreadWapper.class).getRealLizesFirst();
        return Objects.isNull(threadWapper) ? taskActuator : threadWapper.wrapper(taskActuator);
    }

    /**
     * End of single mission line
     * 结束单条任务链
     *
     * @param subtasks the subtasks
     * @throws Exception the exception
     */
    public void stopSingleTaskLine(List<AsyncTask> subtasks) throws Exception {
        TaskActuator taskActuator = processMap.get(assistantTask);
        for (AsyncTask subtask : subtasks) {
            rtDept(subtask, taskActuator);
        }
    }


    /**
     * Rt dept.
     *
     * @param task            task
     * @param terminationTask the terminationTask
     * @throws Exception the exception
     */
    public void rtDept(AsyncTask task, TaskActuator terminationTask) throws Exception {
        if (task instanceof TaskTrigger.AssistantTask) {
            terminationTask.releasingDependency();
            if (!terminationTask.hasUnsatisfiedDependcies()) {
                terminationTask.call();
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
        asyncResult.setExecuteCode(expCode.get());
        asyncResult.setCusCode(cusCode);
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

    /**
     * Gets timer listeners.
     *
     * @return the timer listeners
     */
    public Map<Class<?>, Reference<GobrsTimer.TimerListener>> getTimerListeners() {
        return timerListeners;
    }

    /**
     * Gets future tasks map.
     *
     * @return the future tasks map
     */
    public Map<AsyncTask<P, R>, Future<?>> getFutureTasksMap() {
        return futureTasksMap;
    }

    /**
     * Gets interruptflag.
     *
     * @return the interruptflag
     */
    public AtomicInteger getINTERRUPTFLAG() {
        return INTERRUPTFLAG;
    }

    /**
     * Gets cus code.
     *
     * @return the cus code
     */
    public Integer getCusCode() {
        return cusCode;
    }

    /**
     * Sets cus code.
     *
     * @param cusCode the cus code
     */
    public void setCusCode(Integer cusCode) {
        this.cusCode = cusCode;
    }
}
