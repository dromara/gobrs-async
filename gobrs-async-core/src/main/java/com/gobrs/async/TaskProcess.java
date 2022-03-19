package com.gobrs.async;

/**
 * @program: gobrs-async-starter
 * @ClassName EventProcess
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/

import com.gobrs.async.domain.AsyncParam;
import com.gobrs.async.task.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class TaskProcess implements Runnable, Cloneable {

    protected TaskLoader taskLoader;

    /**
     * Tasks to be performed
     */
    private final AsyncTask task;

    private volatile int unsatisfiedDepdendings;

    /**
     * Dependent task
     */
    private final List<AsyncTask> dependedEventHandlers;

    private AsyncParam param;

    private Lock lock;

    TaskProcess(AsyncTask eventHandler, int depdending,
                List<AsyncTask> dependedEventHandlers) {
        this.task = eventHandler;
        this.unsatisfiedDepdendings = depdending;
        this.dependedEventHandlers = dependedEventHandlers;
    }

    /**
     * Initialize the object cloned from prototype.
     *
     * @param taskLoader
     * @param param
     */
    void init(TaskLoader taskLoader, AsyncParam param) {
        this.taskLoader = taskLoader;
        this.param = param;
    }

    @Override
    public void run() {
        try {
            task.task(param);
            //Fix bug find by zhulixin@jd.com which would block processes already satisfy running conditions.
            if (dependedEventHandlers != null) {
                List<TaskProcess> readyProcesses = new ArrayList<TaskProcess>(dependedEventHandlers.size());
                for (int i = 0; i < dependedEventHandlers.size(); i++) {
                    TaskProcess process = taskLoader
                            .getProcess(dependedEventHandlers.get(i));
                    if (process.decreaseUnsatisfiedDependcies() == 0) {
                        readyProcesses.add(process);
                    }
                }
                if (readyProcesses.size() > 0) {
                    for (int i = (readyProcesses.size() - 1); i > 0; i--) {
                        taskLoader.startProcess(readyProcesses.get(i));
                    }
                    readyProcesses.get(0).run();
                }
            }
        } catch (Exception e) {
            taskLoader.markAsError(e);
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

}
