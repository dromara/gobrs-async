package com.gobrs.async.core;

import com.gobrs.async.core.callback.ErrorCallback;
import com.gobrs.async.core.common.domain.AnyConditionResult;
import com.gobrs.async.core.common.domain.AsyncParam;
import com.gobrs.async.core.common.domain.TaskResult;
import com.gobrs.async.core.common.domain.TaskStatus;
import com.gobrs.async.core.common.enums.ResultState;
import com.gobrs.async.core.common.exception.GobrsForceStopException;
import com.gobrs.async.core.config.ConfigManager;
import com.gobrs.async.core.log.TraceUtil;
import com.gobrs.async.core.task.AsyncTask;
import com.gobrs.async.core.task.TaskUtil;
import com.gobrs.async.core.timer.GobrsFutureTask;
import com.gobrs.async.core.timer.GobrsTimer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.ref.Reference;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.gobrs.async.core.common.def.DefaultConfig.*;
import static com.gobrs.async.core.task.ReUsing.reusing;
import static com.gobrs.async.core.timer.GobrsFutureTask.STOP_STAMP;

/**
 * The type Task actuator.
 *
 * @param <Result> the type parameter
 */
@Slf4j
public class TaskActuator<Result> implements Callable<Result>, Cloneable {

    /**
     * The Logger.
     */

    /**
     * The Support.
     */
    public TaskSupport support;

    /**
     * Tasks to be performed
     */
    public final AsyncTask task;


    /**
     * Upstream dependent quantity
     */
    public volatile int upstreamDepdends;

    /**
     * depend com.gobrs.async.com.gobrs.async.test.task
     */
    public final List<AsyncTask> subTasks;

    /**
     * The Param.
     */
    public AsyncParam param;

    private Lock lock;

    private Map<AsyncTask, List<AsyncTask>> upwardTasksMap;


    /**
     * Instantiates a new Task actuator.
     *
     * @param asyncTask the async com.gobrs.async.com.gobrs.async.test.task
     * @param depends   the depends
     * @param subTasks  the sub tasks
     */
    TaskActuator(AsyncTask asyncTask, int depends, List<AsyncTask> subTasks) {
        this.task = asyncTask;
        this.upstreamDepdends = depends > 1 & task.isAny() ? 1 : depends;
        this.subTasks = subTasks;
    }

    /**
     * Instantiates a new Task actuator.
     *
     * @param asyncTask      the async com.gobrs.async.com.gobrs.async.test.task
     * @param depends        the depends
     * @param subTasks       the sub tasks
     * @param upwardTasksMap the upward tasks map
     */
    TaskActuator(AsyncTask asyncTask, int depends, List<AsyncTask> subTasks, Map<AsyncTask, List<AsyncTask>> upwardTasksMap) {
        this.task = asyncTask;
        this.upstreamDepdends = depends > 1 & task.isAny() ? 1 : depends;
        this.subTasks = subTasks;
        this.upwardTasksMap = upwardTasksMap;
    }


    /**
     * Initialize the object cloned from prototype.
     *
     * @param support the support
     * @param param   the param
     */
    void init(TaskSupport support, AsyncParam param) {
        this.support = support;
        this.param = param;
    }

    @Override
    public Result call() {

        Object parameter = getParameter(task);

        preparation();

        TaskLoader taskLoader = support.getTaskLoader();

        /**
         * If the conditions are not met
         * no execution is performed
         */
        Object result = null;
        try {


            /**
             * 判断任务是否有必要执行
             * 1、necessary 返回true
             * 2、如果具备执行结果 则无需执行
             */
            if (executeNecessary(parameter, task)) {

                task.prepare(parameter);

                /**
                 * Unified front intercept
                 * 统一前置处理
                 */
                taskLoader.preInterceptor(parameter, task.getName());

                /**
                 * Perform a com.gobrs.async.com.gobrs.async.test.task
                 * 执行核心任务处理
                 */
                result = task.taskAdapter(parameter, support);

                /**
                 * 状态改变
                 */
                change();

                /**
                 * 数量统计
                 */
                statisticsOptimalCount();

                /**
                 * Post-processing of tasks
                 * 后置任务
                 */
                taskLoader.postInterceptor(result, task.getName());

                /**
                 * Setting Task Results
                 * 设置任务结果
                 */
                if (ConfigManager.getGlobalConfig().isParamContext()) {
                    support.getResultMap().put(task.getClass(), buildSuccessResult(result));
                }

                /**
                 * Success com.gobrs.async.callback
                 * 执行成功回调
                 */
                task.onSuccess(support);
            }

            noRepeat(taskLoader, result);

        } catch (Exception e) {
            try {
                exceptionProcess(parameter, taskLoader, e);
            } catch (Exception exception) {
                if (log.isErrorEnabled()) {
                    log.error("<{}> [{}] exceptionProcess error {} ", TraceUtil.get(), task.getName(), e);
                }
                taskLoader.stopSingleTaskLine(subTasks);
            }
        } finally {
            clear();
            futureStopRelease(parameter, taskLoader);

        }
        return (Result) result;
    }

    /**
     * 根据中断位强制释放资源 针对开发者使用死循环等问题fix
     *
     * @param taskLoader
     */
    private void futureStopRelease(Object parameter, TaskLoader taskLoader) {
        Future<?> future = (Future<?>) taskLoader.getFutureTasksMap().get(task);
        if (future instanceof GobrsFutureTask) {
            Integer syncState = ((GobrsFutureTask<?>) future).getSyncState();
            if (syncState == STOP_STAMP) {
                releaseFutureTasks();
                onDoTask(parameter, taskLoader, new GobrsForceStopException(String.format(" task %s force stop error", task.getName())));
            }
        }
    }

    private void change() {
        support.getStatus(task.getClass()).compareAndSet(TASK_INITIALIZE, TASK_FINISH);
    }


    private void clear() {
        Reference<GobrsTimer.TimerListener> listenerReference = getListenerReference();
        if (Objects.nonNull(listenerReference)) {
            listenerReference.clear();
        }
    }

    private void releaseFutureTasks() {
        Map<AsyncTask<?,?>, Future<?>> futureTasksMap = support.getTaskLoader().getFutureTasksMap();
        futureTasksMap.remove(task);
    }

    private Reference<GobrsTimer.TimerListener> getListenerReference() {
        Map<Class<?>, Reference<GobrsTimer.TimerListener>> timerListeners = support.getTaskLoader().getTimerListeners();
        return timerListeners.get(task.getClass());
    }

    private boolean executeNecessary(Object parameter, AsyncTask task) {
        return task.necessary(parameter, support) && (Objects.isNull(support.getResultMap().get(task.getClass())));
    }

    /**
     * Determine whether the process is interrupted
     * 判断当前任务是否已经执行过
     */
    private void noRepeat(TaskLoader taskLoader, Object result) {
        if (taskLoader.isRunning().get()) {
            nextTaskByCase(taskLoader, result);
        }
    }

    /**
     * 异常处理
     *
     * @param parameter
     * @param taskLoader
     * @param e
     */
    private void exceptionProcess(Object parameter, TaskLoader taskLoader, Exception e) {

        Optimal.optimalCount(support.taskLoader);

        if (!retryTask(parameter, taskLoader)) {

            support.getResultMap().put(task.getClass(), buildErrorResult(null, e));

            /**
             * transaction com.gobrs.async.com.gobrs.async.test.task
             * 事物任务
             */
            transaction(taskLoader);

            onDoTask(parameter, taskLoader, e);
        }
    }


    private void onDoTask(Object parameter, TaskLoader taskLoader, Exception e) {
        task.onFailureTrace(support, e);
        /**
         * A single com.gobrs.async.com.gobrs.async.test.task com.gobrs.async.exception interrupts the entire process
         * 配置 taskInterrupt = true 则某一任务异常后结束整个任务流程 默认 false
         */
        if (ConfigManager.getRule(taskLoader.getRuleName()).isTaskInterrupt()) {

            taskLoader.errorInterrupted(errorCallback(parameter, e, support, task));

        } else {

            taskLoader.error(errorCallback(parameter, e, support, task));

            /**
             * 当任务失败 是否继续执行子任务
             */
            if (task.isFailSubExec()) {
                nextTask(taskLoader, TaskUtil.defaultAnyCondition(false));
            } else {
                if (!CollectionUtils.isEmpty(taskLoader.getOptionalTasks()) || TaskUtil.multipleDependencies(upwardTasksMap, subTasks)) {
                    nextTask(taskLoader, TaskUtil.defaultAnyCondition(false));
                } else {
                    taskLoader.stopSingleTaskLine(subTasks);
                }

            }
        }
    }

    /**
     * Execute tasks based on conditions
     * 根据条件执行任务
     *
     * @param taskLoader
     * @param result
     */
    private void nextTaskByCase(TaskLoader taskLoader, Object result) {
        if (result instanceof AnyConditionResult) {
            nextTask(taskLoader, (AnyConditionResult) result);
            return;
        }
        nextTask(taskLoader);
    }


    /**
     * 数量统计
     */
    private void statisticsOptimalCount() {
        Optimal.optimalCount(support.taskLoader);
    }

    /**
     * 执行任务准备阶段
     */
    private void preparation() {

        if (task.isExclusive()) {

            List<AsyncTask> asyncTaskList = upwardTasksMap.get(task);

            Map<AsyncTask<?, Result>, Future<?>> futureMaps = support.getTaskLoader().getFutureTasksMap();

            futureMaps.forEach((x, y) -> {

                if (asyncTaskList.contains(x)) {
                    y.cancel(true);
                }

            });

        }
    }

    /**
     * 获取任务参数
     *
     * @return
     */
    private Object getParameter(AsyncTask task) {

        Object parameter = param.get();

        if (parameter instanceof Map) {

            Object param = ((Map<?, ?>) parameter).get(task.getClass());

            return param == null ? ((Map<?, ?>) parameter).get(task.getClass().getName()) : param;
        }
        return parameter;
    }


    /**
     * 任务重试 必须注解开启
     *
     * @param parameter
     * @param taskLoader
     * @return
     */
    private boolean retryTask(Object parameter, TaskLoader taskLoader) {
        try {
            AtomicInteger retryCounts = support.getStatus(task.getClass()).getRetryCounts();

            /**
             * 单任务超时判断
             */
            TaskStatus status = getTaskSupport().getStatus(task.getClass());

            if ((status.getStatus().get() == TASK_INITIALIZE) && task.getRetryCount() > 1 && task.getRetryCount() > retryCounts.get()) {

                retryCounts.incrementAndGet();

                doTaskWithRetryConditional(parameter, taskLoader);

                if (task.isFailSubExec()) {

                    nextTask(taskLoader);

                }
                return true;
            }
            return false;

        } catch (Exception exception) {
            return retryTask(parameter, taskLoader);
        }
    }

    /**
     * 根据条件 选择性任务重试
     *
     * @param parameter
     * @param taskLoader
     */
    private void doTaskWithRetryConditional(Object parameter, TaskLoader taskLoader) throws Exception {

        /**
         * Perform a com.gobrs.async.com.gobrs.async.test.task
         */
        Object result = task.taskAdapter(parameter, support);

        /**
         * 状态改变
         */
        change();

        try {
            /**
             * Post-processing of tasks
             */
            taskLoader.postInterceptor(result, task.getName());

            /**
             * Setting Task Results
             */
            if (ConfigManager.getGlobalConfig().isParamContext()) {
                support.getResultMap().put(task.getClass(), buildSuccessResult(result));
            }
            /**
             * Success com.gobrs.async.callback
             */
            task.onSuccess(support);
        } catch (Exception ex) {
            // todo com.gobrs.async.log
        }
    }


    /**
     * Move on to the next com.gobrs.async.com.gobrs.async.test.task
     *
     * @param taskLoader the com.gobrs.async.com.gobrs.async.test.task loader
     */
    public void nextTask(TaskLoader taskLoader) {
        nextTask(taskLoader, TaskUtil.defaultAnyCondition());
    }

    /**
     * Next com.gobrs.async.com.gobrs.async.test.task.
     * 执行下一任务 （子任务）
     *
     * @param taskLoader      the com.gobrs.async.com.gobrs.async.test.task loader
     * @param conditionResult the com.gobrs.async.com.gobrs.async.test.task conditionResult
     */
    public void nextTask(TaskLoader taskLoader, AnyConditionResult conditionResult) {

        if (!CollectionUtils.isEmpty(subTasks)) {
            for (int i = 0; i < subTasks.size(); i++) {
                TaskActuator process = taskLoader
                        .getProcess(subTasks.get(i));
                Set<AsyncTask> optionalTasks = taskLoader.getOptionalTasks();

                boolean continueExec = Optimal.ifContinue(optionalTasks, taskLoader, process);

                if (!continueExec) {
                    return;
                }
                /**
                 * Check whether the subtask depends on a com.gobrs.async.com.gobrs.async.test.task that has been executed
                 * The number of tasks that it depends on to get to this point minus one
                 */
                if (process.task.isAnyCondition()) {
                    if (process.releasingDependency() == 0 || conditionResult.getState()) {
                        synchronized (process.task) {
                            Boolean aBoolean = (Boolean) taskLoader.anyConditionProx.get(process);
                            if (Objects.isNull(aBoolean)) {
                                taskLoader.anyConditionProx.put(process, true);
                                doTask(taskLoader, process, optionalTasks, isCycleThread(i, process));
                            }
                        }
                    }
                } else {
                    if (process.releasingDependency() == 0) {
                        doTask(taskLoader, process, optionalTasks, isCycleThread(i, process));
                    }
                }
            }
        }
    }

    /**
     * 线程复用
     * 1、最后一个子任务使用父任务的线程
     *
     * @param i
     * @return
     */
    private boolean isCycleThread(int i, TaskActuator taskActuator) {
        return i == subTasks.size() - 1;
    }

    /**
     * cycleThread 线程复用
     * A->C,D
     * 此时 C会使用A的线程继续执行任务 而不会再开启线程 节省了线程开销和线程上下文切换
     *
     * @param taskLoader
     * @param process
     * @param optionalTasks
     */
    private void doTask(TaskLoader taskLoader, TaskActuator process, Set<AsyncTask> optionalTasks, boolean cycleThread) {
        process(taskLoader, process, optionalTasks, cycleThread);
    }


    private void process(TaskLoader taskLoader, TaskActuator process, Set<AsyncTask> optionalTasks, boolean cycleThread) {
        if (Objects.nonNull(optionalTasks)) {
            if (optionalTasks.contains(process.getTask())) {
                doProcess(taskLoader, process, cycleThread);
            }
        } else {
            doProcess(taskLoader, process, cycleThread);
        }
    }

    /**
     * 向下执行
     *
     * @param taskLoader
     * @param process
     * @param cycleThread
     */
    private void doProcess(TaskLoader taskLoader, TaskActuator process, boolean cycleThread) {
        /**
         * retry open thread for task timeout manager
         */
        if (cycleThread && reusing(this) && reusing(process)) {
            /**
             * Thread reuse saves context switching
             */
            process.call();
        } else {
            taskLoader.startProcess(process);
        }
    }

    /**
     * Gets tasks without any dependencies
     * 是否还有自身所依赖的任务
     *
     * @return boolean boolean
     */
    boolean hasUnsatisfiedDependcies() {
        lock.lock();
        try {
            return upstreamDepdends != 0;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Release the number of dependent tasks
     * 释放一个依赖任务
     *
     * @return int int
     */
    public int releasingDependency() {
        lock.lock();
        try {
            return --upstreamDepdends;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object clone() {
        try {
            TaskActuator<?> cloned = (TaskActuator<?>) super.clone();
            cloned.lock = new ReentrantLock();
            return cloned;
        } catch (Exception e) {
            throw new InternalError();
        }
    }


    /**
     * Data transaction
     * 事务
     */
    private void transaction(TaskLoader taskLoader) {
        if (ConfigManager.getRule(taskLoader.getRuleName()).isTransaction()) {

            if (!this.task.isCallback()) {
                return;
            }
            /**
             * Get the parent com.gobrs.async.com.gobrs.async.test.task that the com.gobrs.async.com.gobrs.async.test.task depends on
             */
            List<AsyncTask> asyncTaskList = upwardTasksMap.get(this.task);
            if (asyncTaskList == null || asyncTaskList.isEmpty()) {
                return;
            }

            support.getExecutorService().execute(() -> rollback(asyncTaskList, support));
        }
    }


    /**
     * 业务回滚
     *
     * @param asyncTasks
     * @param support
     */
    private void rollback(List<AsyncTask> asyncTasks, TaskSupport support) {
        for (AsyncTask asyncTask : asyncTasks) {
            try {
                if (support.getParam() instanceof Map) {
                    asyncTask.rollback(((Map<?, ?>) support.getParam()).get(this.getClass()));
                } else {
                    asyncTask.rollback(support.getParam());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            /**
             * Tasks that the parent com.gobrs.async.com.gobrs.async.test.task depends on recursively roll back
             *
             */
            List<AsyncTask> asyncTaskList = upwardTasksMap.get(asyncTask);
            rollback(asyncTaskList, support);
        }
    }


    /**
     * Gets com.gobrs.async.com.gobrs.async.test.task.
     *
     * @return the com.gobrs.async.com.gobrs.async.test.task
     */
    public AsyncTask getTask() {
        return task;
    }

    /**
     * Gets com.gobrs.async.com.gobrs.async.test.task support.
     *
     * @return the com.gobrs.async.com.gobrs.async.test.task support
     */
    public TaskSupport getTaskSupport() {
        return support;
    }

    /**
     * Gets task status.
     *
     * @return the task status
     *
     */
    public TaskStatus taskStatus() {
        return support.getStatus(task.getClass());
    }


    /**
     * Sets com.gobrs.async.com.gobrs.async.test.task support.
     *
     * @param taskSupport the com.gobrs.async.com.gobrs.async.test.task support
     */
    public void setTaskSupport(TaskSupport taskSupport) {
        this.support = taskSupport;
    }


    /**
     * Build com.gobrs.async.com.gobrs.async.test.task result com.gobrs.async.com.gobrs.async.test.task result.
     *
     * @param parameter   the parameter
     * @param resultState the result state
     * @param ex          the ex
     * @return the com.gobrs.async.com.gobrs.async.test.task result
     */
    public TaskResult buildTaskResult(Object parameter, ResultState resultState, Exception ex) {
        return new TaskResult(parameter, resultState, ex);
    }


    /**
     * Build success result com.gobrs.async.com.gobrs.async.test.task result.
     *
     * @param result the result
     * @return the com.gobrs.async.com.gobrs.async.test.task result
     */
    public TaskResult buildSuccessResult(Object result) {
        return new TaskResult(result, ResultState.SUCCESS, null);
    }


    /**
     * Build error result com.gobrs.async.com.gobrs.async.test.task result.
     *
     * @param result the result
     * @param ex     the ex
     * @return the com.gobrs.async.com.gobrs.async.test.task result
     */
    public TaskResult buildErrorResult(Object result, Exception ex) {
        return new TaskResult(result, ResultState.SUCCESS, ex);
    }

    /**
     * Error com.gobrs.async.callback error com.gobrs.async.callback.
     *
     * @param result    the parameter
     * @param e         the e
     * @param support   the support
     * @param asyncTask the async com.gobrs.async.com.gobrs.async.test.task
     * @return the error com.gobrs.async.callback
     */
    public ErrorCallback errorCallback(Object result, Exception e, TaskSupport support, AsyncTask asyncTask) {
        return new ErrorCallback(param, e, support, asyncTask);
    }

    /**
     * Gets upstream depdends.
     *
     * @return the upstream depdends
     */
    public int getUpstreamDepdends() {
        return upstreamDepdends;
    }

    /**
     * Sets upstream depdends.
     *
     * @param upstreamDepdends the upstream depdends
     */
    public void setUpstreamDepdends(int upstreamDepdends) {
        this.upstreamDepdends = upstreamDepdends;
    }

}
