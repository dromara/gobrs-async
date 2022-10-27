package com.gobrs.async;

/**
 * @program: gobrs-async-starter
 * @ClassName TaskActuator
 * @description: task executor task decorator
 * @author: sizegang
 * @create: 2022-03-16
 **/

import com.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.gobrs.async.callback.ErrorCallback;
import com.gobrs.async.domain.AnyConditionResult;
import com.gobrs.async.domain.AsyncParam;
import com.gobrs.async.domain.TaskResult;
import com.gobrs.async.enums.ResultState;
import com.gobrs.async.task.AsyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.gobrs.async.util.TaskUtil.defaultAnyCondition;
import static com.gobrs.async.util.TaskUtil.multipleDependencies;

/**
 * The type Task actuator.
 */
public class TaskActuator implements Runnable, Cloneable {

    /**
     * The Logger.
     */
    Logger logger = LoggerFactory.getLogger(TaskActuator.class);

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
     * depend task
     */
    public final List<AsyncTask> subTasks;

    /**
     * The Param.
     */
    public AsyncParam param;

    private Lock lock;

    private Map<AsyncTask, List<AsyncTask>> upwardTasksMap;


    private GobrsAsyncProperties gobrsAsyncProperties;

    /**
     * 执行状态
     */
    private AtomicInteger state;


    /**
     * Instantiates a new Task actuator.
     *
     * @param asyncTask the async task
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
     * @param asyncTask      the async task
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
    public void run() {

        Object parameter = getParameter();

        preparation();

        TaskLoader taskLoader = support.getTaskLoader();

        try {
            /**
             * If the conditions are not met
             * no execution is performed
             */
            Object result = null;

            /**
             * 判断任务是否有必要执行
             * 1、nessary 返回true
             * 2、如果具备执行结果 则无需执行
             */
            if (task.nessary(parameter, support) && (Objects.isNull(support.getResultMap().get(task.getClass())))) {

                task.prepare(parameter);

                /**
                 * Unified front intercept
                 * 统一前置处理
                 */
                taskLoader.preInterceptor(parameter, task.getName());

                /**
                 * Perform a task
                 * 执行核心任务处理
                 */
                result = task.task(parameter, support);


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
                if (gobrsAsyncProperties.isParamContext()) {
                    support.getResultMap().put(task.getClass(), buildSuccessResult(result));
                }

                /**
                 * Success callback
                 * 执行成功回调
                 */
                task.onSuccess(support);
            }
            /**
             * Determine whether the process is interrupted
             * 判断当前任务是否已经执行过
             */
            if (taskLoader.isRunning().get()) {
                nextTaskByCase(taskLoader, result);
            }
        } catch (Exception e) {

            Optimal.optimalCount(support.taskLoader);

            logger.error("【Gobrs-Async print error】 taskName{} error{}", task.getName(), e);

            state = new AtomicInteger(1);

            if (!retryTask(parameter, taskLoader)) {
                support.getResultMap().put(task.getClass(), buildErrorResult(null, e));

                try {
                    task.onFailureTrace(support, e);
                } catch (Exception ex) {
                    // Failed events are not processed
                    logger.error("task onFailureTrace process is error {}", ex);
                }
                /**
                 * transaction task
                 * 事物任务
                 */
                transaction();

                /**
                 * A single task exception interrupts the entire process
                 * 配置 taskInterrupt = true 则某一任务异常后结束整个任务流程 默认 false
                 */
                if (gobrsAsyncProperties.isTaskInterrupt()) {

                    taskLoader.errorInterrupted(errorCallback(parameter, e, support, task));

                } else {

                    taskLoader.error(errorCallback(parameter, e, support, task));

                    /**
                     * 当然任务失败 是否继续执行子任务
                     */
                    if (task.isFailSubExec()) {
                        nextTask(taskLoader, defaultAnyCondition(false));
                    } else {
                        if (!CollectionUtils.isEmpty(taskLoader.getOptionalTasks()) || multipleDependencies(upwardTasksMap, subTasks)) {
                            nextTask(taskLoader, defaultAnyCondition(false));
                        } else {
                            taskLoader.stopSingleTaskLine(subTasks);
                        }

                    }
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

            Map<AsyncTask, Future> futuresAsync = support.getTaskLoader().futureMaps;

            futuresAsync.forEach((x, y) -> {

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
    private Object getParameter() {

        Object parameter = param.get();

        if (parameter instanceof Map) {

            Object param = ((Map<?, ?>) parameter).get(task.getClass());

            if (Objects.nonNull(param)) {
                param = ((Map<?, ?>) parameter).get(task.getClass());

                return param == null ? ((Map<?, ?>) parameter).get(task.getClass().getName()) : param;
            }
        }
        return parameter;
    }


    /**
     * 任务重试 必须注解开启
     * @param parameter
     * @param taskLoader
     * @return
     */
    private boolean retryTask(Object parameter, TaskLoader taskLoader) {
        try {
            if (task.getRetryCount() > 1 && task.getRetryCount() >= state.get()) {

                state.incrementAndGet();

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
     * @param parameter
     * @param taskLoader
     */
    private void doTaskWithRetryConditional(Object parameter, TaskLoader taskLoader) {

        /**
         * Perform a task
         */
        Object result = task.task(parameter, support);

        try {
            /**
             * Post-processing of tasks
             */
            taskLoader.postInterceptor(result, task.getName());

            /**
             * Setting Task Results
             */
            if (gobrsAsyncProperties.isParamContext()) {
                support.getResultMap().put(task.getClass(), buildSuccessResult(result));
            }
            /**
             * Success callback
             */
            task.onSuccess(support);
        } catch (Exception ex) {
            // todo log
        }
    }


    /**
     * Move on to the next task
     *
     * @param taskLoader the task loader
     */
    public void nextTask(TaskLoader taskLoader) {
        nextTask(taskLoader, defaultAnyCondition());
    }

    /**
     * Next task.
     * 执行下一任务 （子任务）
     *
     * @param taskLoader      the task loader
     * @param conditionResult the task conditionResult
     */
    public void nextTask(TaskLoader taskLoader, AnyConditionResult conditionResult) {

        if (!CollectionUtils.isEmpty(subTasks)) {
            boolean hasUsedSynRunTimeOnce = false;
            for (int i = 0; i < subTasks.size(); i++) {
                TaskActuator process = taskLoader
                        .getProcess(subTasks.get(i));
                Set<AsyncTask> optionalTasks = taskLoader.getOptionalTasks();

                boolean continueExec = Optimal.ifContinue(optionalTasks, taskLoader, process);

                if (!continueExec) {
                    return;
                }
                /**
                 * Check whether the subtask depends on a task that has been executed
                 * The number of tasks that it depends on to get to this point minus one
                 */
                if (process.task.isAnyCondition()) {
                    if (process.releasingDependency() == 0 || conditionResult.getState()) {
                        synchronized (process.task) {
                            Boolean aBoolean = taskLoader.anyConditionProx.get(process);
                            if (Objects.isNull(aBoolean)) {
                                taskLoader.anyConditionProx.put(process, true);
                                doTask(taskLoader, process, optionalTasks, hasUsedSynRunTimeOnce);
                            }
                        }
                    }
                } else {
                    if (process.releasingDependency() == 0) {
                        doTask(taskLoader, process, optionalTasks, hasUsedSynRunTimeOnce);
                    }
                }
            }
        }
    }

    /**
     *  hasUsedSynRunTimeOnce 线程复用
     *  A->C,D
     *  此时 C会使用A的线程继续执行任务 而不会再开启线程 节省了线程开销和线程上下文切换
     *
     * @param taskLoader
     * @param process
     * @param optionalTasks
     */
    private void doTask(TaskLoader taskLoader, TaskActuator process, Set<AsyncTask> optionalTasks, boolean hasUsedSynRunTimeOnce) {
        if (!hasUsedSynRunTimeOnce && !process.task.isExclusive()) {
            hasUsedSynRunTimeOnce = true;
            process.run();
        } else {
            doProcess(taskLoader, process, optionalTasks);
        }
    }


    private void doProcess(TaskLoader taskLoader, TaskActuator process, Set<AsyncTask> optionalTasks) {
        if (Objects.nonNull(optionalTasks)) {
            if (optionalTasks.contains(process.getTask())) {
                taskLoader.startProcess(process);
            }
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
            TaskActuator cloned = (TaskActuator) super.clone();
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
    private void transaction() {
        if (gobrsAsyncProperties.isTransaction()) {

            if (!this.task.isCallback()) {
                return;
            }
            /**
             * Get the parent task that the task depends on
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
             * Tasks that the parent task depends on recursively roll back
             *
             */
            List<AsyncTask> asyncTaskList = upwardTasksMap.get(asyncTask);
            rollback(asyncTaskList, support);
        }
    }


    /**
     * Gets task.
     *
     * @return the task
     */
    public AsyncTask getTask() {
        return task;
    }

    /**
     * Gets task support.
     *
     * @return the task support
     */
    public TaskSupport getTaskSupport() {
        return support;
    }

    /**
     * Sets task support.
     *
     * @param taskSupport the task support
     */
    public void setTaskSupport(TaskSupport taskSupport) {
        this.support = taskSupport;
    }


    /**
     * Build task result task result.
     *
     * @param parameter   the parameter
     * @param resultState the result state
     * @param ex          the ex
     * @return the task result
     */
    public TaskResult buildTaskResult(Object parameter, ResultState resultState, Exception ex) {
        return new TaskResult(parameter, resultState, ex);
    }


    /**
     * Build success result task result.
     *
     * @param result the result
     * @return the task result
     */
    public TaskResult buildSuccessResult(Object result) {
        return new TaskResult(result, ResultState.SUCCESS, null);
    }


    /**
     * Build error result task result.
     *
     * @param result the result
     * @param ex     the ex
     * @return the task result
     */
    public TaskResult buildErrorResult(Object result, Exception ex) {
        return new TaskResult(result, ResultState.SUCCESS, ex);
    }

    /**
     * Error callback error callback.
     *
     * @param result    the parameter
     * @param e         the e
     * @param support   the support
     * @param asyncTask the async task
     * @return the error callback
     */
    public ErrorCallback errorCallback(Object result, Exception e, TaskSupport support, AsyncTask asyncTask) {
        return new ErrorCallback(param, e, support, asyncTask);
    }

    /**
     * Gets gobrs async properties.
     *
     * @return the gobrs async properties
     */
    public GobrsAsyncProperties getGobrsAsyncProperties() {
        return gobrsAsyncProperties;
    }

    /**
     * Sets gobrs async properties.
     *
     * @param gobrsAsyncProperties the gobrs async properties
     */
    public void setGobrsAsyncProperties(GobrsAsyncProperties gobrsAsyncProperties) {
        this.gobrsAsyncProperties = gobrsAsyncProperties;
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
