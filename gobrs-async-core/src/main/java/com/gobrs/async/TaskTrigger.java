package com.gobrs.async;

import com.gobrs.async.domain.AsyncParam;
import com.gobrs.async.task.AsyncTask;

import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * @program: gobrs-async-starter
 * @description: Task preloader
 * @author: sizegang
 * @create: 2022-03-16
 **/

class TaskTrigger {

    private final TaskBus taskBus;

    private final ExecutorService executorService;

    private IdentityHashMap<AsyncTask, TaskProcess> prepareTaskMap = new IdentityHashMap<>();

    TaskTrigger(TaskBus taskBus, ExecutorService executorService) {
        this.taskBus = taskBus;
        this.executorService = executorService;
        prepare();
    }

    private void prepare() {

        /**
         * Copy depended event handler map from script map to avoid aside effect
         * to Script.
         */
        Map<AsyncTask, List<AsyncTask>> dependTasks = copyDependTasks(taskBus.getdenpendedEventHandlers());
        /**
         * Compute depending event handler map. We can compute only depending
         * count, but that is not the key to the performance.
         */
        Map<AsyncTask, List<AsyncTask>> excessiveTaskMap = new HashMap<>();

        for (AsyncTask task : dependTasks.keySet()) {
            excessiveTaskMap.put(task, new ArrayList<>(1));
        }
        for (AsyncTask task : dependTasks.keySet()) {
            for (AsyncTask depended : dependTasks.get(task)) {
                excessiveTaskMap.get(depended).add(task);
            }
        }

        Starter starter = new Starter();
        List<AsyncTask> endDependTask = new ArrayList<>(1);

        for (AsyncTask task : dependTasks.keySet()) {
            List<AsyncTask> dTasks = dependTasks.get(task);
            if (dTasks.isEmpty()) {
                endDependTask.add(task);
                dependTasks.get(task).add(starter);
            }
        }
        dependTasks.put(starter, new ArrayList<>(0));
        excessiveTaskMap.put(starter, endDependTask);

        /**
         * Prepare process prototypes
         */
        for (AsyncTask task : dependTasks.keySet()) {
            TaskProcess process;
            if (task != starter) {
                process = new TaskProcess(task, excessiveTaskMap.get(task).size(), dependTasks.get(task));
            } else {
                process = new WrapperTaskProcess(task, excessiveTaskMap.get(task).size(), dependTasks.get(task));
            }
            prepareTaskMap.put(task, process);
        }
    }

    private Map<AsyncTask, List<AsyncTask>> copyDependTasks(Map<AsyncTask, List<AsyncTask>> handlerMap) {
        IdentityHashMap<AsyncTask, List<AsyncTask>> rt = new IdentityHashMap<>();
        for (AsyncTask eventHandler : handlerMap.keySet()) {
            rt.put(eventHandler, new ArrayList<>(handlerMap.get(eventHandler)));
        }
        return rt;
    }

    TaskLoader build(AsyncParam param, long timeout) {
        return build(param, timeout, null);
    }

    TaskLoader build(AsyncParam param, long timeout, Callback callback) {
        /**
         * clone Process
         */
        IdentityHashMap<AsyncTask, TaskProcess> newProcessMap = new IdentityHashMap<>(prepareTaskMap.size());
        TaskLoader runtime = new TaskLoader(param, executorService, newProcessMap, callback, timeout);
        for (AsyncTask task : prepareTaskMap.keySet()) {
            TaskProcess newProcess = (TaskProcess) prepareTaskMap
                    .get(task).clone();
            newProcess.init(runtime, param);
            newProcessMap.put(task, newProcess);
        }
        return runtime;
    }

    /**
     * ScriptEndEventHandler will do work to complete the whole ScriptRuntime
     * process.
     */
    private class WrapperTaskProcess extends TaskProcess {

        WrapperTaskProcess(AsyncTask handler, int depdending, List<AsyncTask> dependedTasks) {
            super(handler, depdending, dependedTasks);
        }

        @Override
        public void run() {
            taskLoader.markAsCompleted();
        }

    }

    private class Starter<P, R> implements AsyncTask<P, R> {
        @Override
        public R task(P p) {
            return null;
        }

        @Override
        public boolean nessary(P p) {
            return true;
        }
    }


}
