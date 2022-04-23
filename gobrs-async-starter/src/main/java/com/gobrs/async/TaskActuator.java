package com.gobrs.async;

/**
 * @program: gobrs-async-starter
 * @ClassName TaskActuator
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/

import com.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.gobrs.async.callback.ErrorCallback;
import com.gobrs.async.domain.AsyncParam;
import com.gobrs.async.domain.TaskResult;
import com.gobrs.async.enums.ResultState;
import com.gobrs.async.task.AsyncTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class TaskActuator implements Runnable, Cloneable {
    Logger logger = LoggerFactory.getLogger(TaskActuator.class);
    public TaskSupport support;

    /**
     * Tasks to be performed
     */
    public final AsyncTask task;

    private boolean must = true;


    /**
     * Upstream dependent quantity
     */
    private volatile int upstreamDepdends;

    /**
     * depend task
     */
    private final List<AsyncTask> subTasks;

    public AsyncParam param;

    private Lock lock;

    private Map<AsyncTask, List<AsyncTask>> upwardTasksMap;


    private GobrsAsyncProperties gobrsAsyncProperties;

    private AtomicInteger state;


    TaskActuator(AsyncTask task, int depdending, List<AsyncTask> subTasks) {
        this.task = task;
        this.upstreamDepdends = depdending;
        this.subTasks = subTasks;
    }

    TaskActuator(AsyncTask asyncTask, int depdending, List<AsyncTask> subTasks, Map<AsyncTask, List<AsyncTask>> upwardTasksMap) {
        this.task = asyncTask;
        this.upstreamDepdends = depdending;
        this.subTasks = subTasks;
        this.upwardTasksMap = upwardTasksMap;
    }


    /**
     * Initialize the object cloned from prototype.
     *
     * @param support
     * @param param
     */
    void init(TaskSupport support, AsyncParam param) {
        this.support = support;
        this.param = param;
    }

    @Override
    public void run() {

        Object parameter = param.get();

        if (parameter instanceof Map) {
            parameter = ((Map<?, ?>) parameter).get(this.getClass());
        }

        TaskLoader taskLoader = support.getTaskLoader();
        try {
            /**
             * If the conditions are not met
             * no execution is performed
             */
            if (task.nessary(parameter, support) && support.getResultMap().get(task.getClass()) == null) {

                task.prepare(parameter);

                /**
                 * Unified front intercept
                 */
                taskLoader.preInterceptor(parameter, task.getName());

                /**
                 * Perform a task
                 */
                Object result = task.task(parameter, support);

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
                nextTask(taskLoader);
            }
        } catch (Exception e) {
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
                        nextTask(taskLoader);
                    } else {
                        taskLoader.stopSingleTaskLine(subTasks.size());
                    }
                }
            }

        }
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
     */
    public void nextTask(TaskLoader taskLoader) {
        if (subTasks != null) {
            boolean hasUsedSynRunTimeOnce = false;
            for (int i = 0; i < subTasks.size(); i++) {
                TaskActuator process = taskLoader
                        .getProcess(subTasks.get(i));
                /**
                 * Check whether the subtask depends on a task that has been executed
                 * The number of tasks that it depends on to get to this point minus one
                 */
                if (process.releasingDependency() == 0) {
                    /**
                     * Make the most of your threads
                     * Use the parent thread to perform the first child task
                     */
                    if (!hasUsedSynRunTimeOnce) {
                        hasUsedSynRunTimeOnce = true;
                        process.run();
                    } else {
                        /**
                         * The remaining subtasks open threads using thread pools
                         */
                        taskLoader.startProcess(process);
                    }
                }
            }
        }
    }

    /**
     * Gets tasks without any dependencies
     *
     * @return
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
     * @return
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
            List<AsyncTask> asyncTaskList = upwardTasksMap.get(asyncTask);
            rollback(asyncTaskList, support);
        }
    }


    public AsyncTask getTask() {
        return task;
    }

    public TaskSupport getTaskSupport() {
        return support;
    }

    public void setTaskSupport(TaskSupport taskSupport) {
        this.support = taskSupport;
    }


    public TaskResult buildTaskResult(Object parameter, ResultState resultState, Exception ex) {
        return new TaskResult(parameter, resultState, ex);
    }


    public TaskResult buildSuccessResult(Object parameter) {
        return new TaskResult(parameter, ResultState.SUCCESS, null);
    }


    public TaskResult buildErrorResult(Object parameter, Exception ex) {
        return new TaskResult(parameter, ResultState.SUCCESS, ex);
    }

    public ErrorCallback errorCallback(Object parameter, Exception e, TaskSupport support, AsyncTask asyncTask) {
        return new ErrorCallback(param, e, support, asyncTask);
    }

    public GobrsAsyncProperties getGobrsAsyncProperties() {
        return gobrsAsyncProperties;
    }

    public void setGobrsAsyncProperties(GobrsAsyncProperties gobrsAsyncProperties) {
        this.gobrsAsyncProperties = gobrsAsyncProperties;
    }

    public boolean isMust() {
        return must;
    }

    public void setMust(boolean must) {
        this.must = must;
    }
}
