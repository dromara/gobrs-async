package com.gobrs.async;

/**
 * @program: gobrs-async-starter
 * @ClassName EventProcess
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class TaskProcess implements Runnable, Cloneable {

    public TaskSupport support;

    /**
     * Tasks to be performed
     */
    public final AsyncTask task;

    private boolean must = true;


    private volatile int unsatisfiedDepdendings;

    /**
     * depend task
     */
    private final List<AsyncTask> dependTasks;

    public AsyncParam param;

    private Lock lock;


    private GobrsAsyncProperties gobrsAsyncProperties;


    TaskProcess(AsyncTask eventHandler, int depdending, List<AsyncTask> dependTasks) {
        this.task = eventHandler;
        this.unsatisfiedDepdendings = depdending;
        this.dependTasks = dependTasks;
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
        TaskLoader taskLoader = support.getTaskLoader();
        try {
            /**
             * If the conditions are not met
             * no execution is performed
             */
            if (task.nessary(parameter, support) && support.getResultMap().get(task.getClass()) == null) {
                task.prepare(param);
                Object result = task.task(param.get(), support);
                support.getResultMap().put(task.getClass(), buildSuccessResult(result));
                task.onSuccess(support);
            }
            if (dependTasks != null) {
                List<TaskProcess> readyProcesses = new ArrayList<>(dependTasks.size());
                for (int i = 0; i < dependTasks.size(); i++) {
                    TaskProcess process = taskLoader.getProcess(dependTasks.get(i));
                    /**
                     * No dependent task is executed By configuring
                     */
                    if(gobrsAsyncProperties.isRelyDepend()){
                        if (process.decreaseUnsatisfiedDependcies() == 0) {
                            readyProcesses.add(process);
                        }
                    }else{
                        readyProcesses.add(process);
                    }
                }
                /**
                 * Response to perform
                 */
                if (readyProcesses.size() > 0) {
                    for (int i = (readyProcesses.size() - 1); i > 0; i--) {
                        taskLoader.startProcess(readyProcesses.get(i));
                    }
                    /**
                     * End Task Process
                     */
                    readyProcesses.get(0).run();
                }
            }
        } catch (Exception e) {
            support.getResultMap().put(task.getClass(), buildErrorResult(null, e));
            task.onFail(support);
            if (gobrsAsyncProperties.isTaskInterrupt()) {
                taskLoader.errorInterrupted(errorCallback(parameter, e, support, task));
            } else {
                taskLoader.error(errorCallback(parameter, e, support, task));
            }
        }
    }

    boolean hasUnsatisfiedDependcies() {
        lock.lock();
        try {
            return unsatisfiedDepdendings != 0;
        } finally {
            lock.unlock();
        }
    }

    private int decreaseUnsatisfiedDependcies() {
        lock.lock();
        try {
            return --unsatisfiedDepdendings;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object clone() {
        try {
            TaskProcess cloned = (TaskProcess) super.clone();
            cloned.lock = new ReentrantLock();
            return cloned;
        } catch (Exception e) {
            throw new InternalError();
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
