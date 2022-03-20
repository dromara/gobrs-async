package com.gobrs.async;

import com.gobrs.async.domain.AsyncParam;
import com.gobrs.async.spring.GobrsSpring;
import com.gobrs.async.task.AsyncTask;
import com.gobrs.async.threadpool.GobrsAsyncThreadPoolFactory;

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

    private final ExecutorService executorService = GobrsSpring.getBean(GobrsAsyncThreadPoolFactory.class).getThreadPoolExecutor();

    private IdentityHashMap<AsyncTask, TaskProcess> prepareTaskMap = new IdentityHashMap<>();

    TaskTrigger(TaskFlow taskFlow) {
        this.taskFlow = taskFlow;
        prepare();
    }

    private void prepare() {


        Map<AsyncTask, List<AsyncTask>> downTasksMap = copyDependTasks(taskFlow.getDependsTasks());

        Map<AsyncTask, List<AsyncTask>> upwardTasksMap = new HashMap<>();

        for (AsyncTask task : downTasksMap.keySet()) {
            upwardTasksMap.put(task, new ArrayList<>(1));
        }

        for (AsyncTask task : downTasksMap.keySet()) {
            for (AsyncTask depended : downTasksMap.get(task)) {
                upwardTasksMap.get(depended).add(task);
            }
        }


        Starter starter = new Starter();
        List<AsyncTask> endDependTask = new ArrayList<>(1);

        for (AsyncTask task : downTasksMap.keySet()) {
            List<AsyncTask> dTasks = downTasksMap.get(task);
            if (dTasks.isEmpty()) {
                endDependTask.add(task);
                downTasksMap.get(task).add(starter);
            }
        }
        downTasksMap.put(starter, new ArrayList<>(0));
        upwardTasksMap.put(starter, endDependTask);

        /**
         * Prepare process prototypes
         */
        for (AsyncTask task : downTasksMap.keySet()) {
            TaskProcess process;
            if (task != starter) {
                /**
                 * Each task is executed using a new Processs
                 */
                process = new TaskProcess(task, upwardTasksMap.get(task).size(), downTasksMap.get(task));
            } else {
                /***
                 * completely
                 */
                process = new WrapperTaskProcess(task, upwardTasksMap.get(task).size(), downTasksMap.get(task));
            }
            process.setGobrsAsyncProperties(taskFlow.getGobrsAsyncProperties());
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
        IdentityHashMap<AsyncTask, TaskProcess> newProcessMap = new IdentityHashMap<>(prepareTaskMap.size());
        /**
         * Assign one loader to each task
         */
        TaskLoader loader = new TaskLoader(param, executorService, newProcessMap, timeout);
        TaskSupport support = getSupport(param);
        support.setTaskLoader(loader);

        for (AsyncTask task : prepareTaskMap.keySet()) {
            /**
             * clone Process
             */
            TaskProcess newProcess = (TaskProcess) prepareTaskMap.get(task).clone();
            newProcess.init(support, param);
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
            support.taskLoader.completed();
        }
    }

    private class Starter<P, R> implements AsyncTask<P, R> {

        @Override
        public void prepare(P p) {

        }

        @Override
        public R task(P p, TaskSupport support) {
            return null;
        }

        @Override
        public boolean nessary(P p, TaskSupport support) {
            return true;
        }

        @Override
        public void onSuccess(TaskSupport support) {

        }

        @Override
        public void onFail(TaskSupport support) {

        }
    }

    private TaskSupport getSupport(AsyncParam param) {
        TaskSupport taskSupport = new TaskSupport();
        taskSupport.setParam(param);
        return taskSupport;
    }

}
