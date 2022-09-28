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
import com.gobrs.async.domain.AsyncParam;
import com.gobrs.async.domain.TaskResult;
import com.gobrs.async.enums.ResultState;
import com.gobrs.async.exception.GobrsAsyncException;
import com.gobrs.async.task.AsyncTask;
import com.gobrs.async.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    private AtomicInteger state;

    private volatile AtomicInteger starting = new AtomicInteger(0);


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
            if (task.nessary(parameter, support) && (support.getResultMap().get(task.getClass()) == null || task.isRepeatable())) {

                task.prepare(parameter);

                /**
                 * Unified front intercept
                 */
                taskLoader.preInterceptor(parameter, task.getName());

                /**
                 * Perform a task
                 */
                result = task.task(parameter, support);

                aiirAxamination();

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
            }
            /**
             * Determine whether the process is interrupted
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
                    task.onFail(support);
                } catch (Exception ex) {
                    // Failed events are not processed
                    logger.error("task onFail process is error {}", ex);
                }
                /**
                 * transaction task
                 */
                transaction();

                /**
                 * A single task exception interrupts the entire process
                 */
                if (gobrsAsyncProperties.isTaskInterrupt()) {
                    taskLoader.errorInterrupted(errorCallback(parameter, e, support, task));
                } else {
                    taskLoader.error(errorCallback(parameter, e, support, task));
                    if (task.isFailSubExec()) {
                        nextTask(taskLoader, false);
                    } else {
                        if (!CollectionUtils.isEmpty(taskLoader.getAffirTasks()) || multipleDependencies(upwardTasksMap, subTasks)) {
                            nextTask(taskLoader, false);
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
     *
     * @param taskLoader
     * @param result
     */
    private void nextTaskByCase(TaskLoader taskLoader, Object result) {
        if (result instanceof Boolean) {
            nextTask(taskLoader, (Boolean) result);
            return;
        }
        nextTask(taskLoader);
    }

    private void aiirAxamination() {
        Optimal.optimalCount(support.taskLoader);
    }

    private void preparation() {

        if (task.isExclusive()) {

            List<AsyncTask> asyncTaskList = upwardTasksMap.get(task);

            Map<AsyncTask, Future> futuresAsync = support.getTaskLoader().futuresAsync;

            futuresAsync.forEach((x, y) -> {

                if (asyncTaskList.contains(x)) {

                    y.cancel(true);
                }
            });

        }
    }

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
        nextTask(taskLoader, true);
    }

    /**
     * Next task.
     *
     * @param taskLoader       the task loader
     * @param taskResultStatue the task result statue
     */
    public void nextTask(TaskLoader taskLoader, boolean taskResultStatue) {
        if (subTasks != null) {
            for (int i = 0; i < subTasks.size(); i++) {
                TaskActuator process = taskLoader
                        .getProcess(subTasks.get(i));
                Set<AsyncTask> affirTasks = taskLoader.getAffirTasks();

                boolean continueExec = Optimal.ifContinue(affirTasks, taskLoader, process);

                if (!continueExec) {
                    return;
                }
                /**
                 * Check whether the subtask depends on a task that has been executed
                 * The number of tasks that it depends on to get to this point minus one
                 */
                if (process.task.isAnyCondition()) {
                    if (taskResultStatue) {
                        if (starting.compareAndSet(0, 1)) {
                            doTask(taskLoader, process, affirTasks);
                        }
                    }
                    process.releasingDependency();
                } else {
                    if (process.releasingDependency() == 0) {
                        doTask(taskLoader, process, affirTasks);
                    }
                }
            }
        }
    }

    /**
     * @param taskLoader
     * @param process
     * @param affirTasks
     */
    private void doTask(TaskLoader taskLoader, TaskActuator process, Set<AsyncTask> affirTasks) {
        if (subTasks.size() == 1 && !process.task.isExclusive()) {
            process.run();
        } else {
            doProcess(taskLoader, process, affirTasks);
        }
    }


    private void doProcess(TaskLoader taskLoader, TaskActuator process, Set<AsyncTask> affirTasks) {

        if (affirTasks != null) {
            if (affirTasks.contains(process.getTask())) {
                taskLoader.startProcess(process);
            }
        } else {
            taskLoader.startProcess(process);
        }
    }

    /**
     * Gets tasks without any dependencies
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
     * @param parameter the parameter
     * @return the task result
     */
    public TaskResult buildSuccessResult(Object parameter) {
        return new TaskResult(parameter, ResultState.SUCCESS, null);
    }


    /**
     * Build error result task result.
     *
     * @param parameter the parameter
     * @param ex        the ex
     * @return the task result
     */
    public TaskResult buildErrorResult(Object parameter, Exception ex) {
        return new TaskResult(parameter, ResultState.EXCEPTION, ex);
    }

    /**
     * Error callback error callback.
     *
     * @param parameter the parameter
     * @param e         the e
     * @param support   the support
     * @param asyncTask the async task
     * @return the error callback
     */
    public ErrorCallback errorCallback(Object parameter, Exception e, TaskSupport support, AsyncTask asyncTask) {
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
