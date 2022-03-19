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

    private final TaskFlow taskFlow;

    private final ExecutorService executorService;

    private IdentityHashMap<AsyncTask, TaskProcess> prepareTaskMap = new IdentityHashMap<>();

    TaskTrigger(TaskFlow taskFlow, ExecutorService executorService) {
        this.taskFlow = taskFlow;
        this.executorService = executorService;
        prepare();
    }

    private void prepare() {


        Map<AsyncTask, List<AsyncTask>> dependTasks = copyDependTasks(taskFlow.getDependsTasks());

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
                /**
                 * Each task is executed using a new Processs
                 */
                process = new TaskProcess(task, excessiveTaskMap.get(task).size(), dependTasks.get(task));
            } else {
                /***
                 * completely
                 */
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

    TaskLoader trigger(AsyncParam param, long timeout) {
        return trigger(param, timeout, null);
    }

    TaskLoader trigger(AsyncParam param, long timeout, Callback callback) {
        /**
         * clone Process
         */
        IdentityHashMap<AsyncTask, TaskProcess> newProcessMap = new IdentityHashMap<>(prepareTaskMap.size());
        /**
         * Assign one loader to each task
         */
        TaskLoader loader = new TaskLoader(param, executorService, newProcessMap, callback, timeout, getSupport(param));
        for (AsyncTask task : prepareTaskMap.keySet()) {
            TaskProcess newProcess = (TaskProcess) prepareTaskMap.get(task).clone();
            newProcess.init(loader, param);
            newProcessMap.put(task, newProcess);
        }
        return loader;
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

    private TaskSupport getSupport(AsyncParam param) {
        TaskSupport taskSupport = new TaskSupport();
        taskSupport.setParam(param);
        return taskSupport;
    }

}
