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

class TaskProcess {

    private final TaskFlow taskFlow;

    private final ExecutorService executorService = GobrsSpring.getBean(GobrsAsyncThreadPoolFactory.class).getThreadPoolExecutor();

    private IdentityHashMap<AsyncTask, TaskActuator> prepareTaskMap = new IdentityHashMap<>();

    TaskProcess(TaskFlow taskFlow) {
        this.taskFlow = taskFlow;
        prepare();
    }

    /**
     * Build task dependencies Load the cache for the first time when a project is started, subsequent cache processing is performed only once
     */
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


        AssistantTask starter = new AssistantTask();
        List<AsyncTask> noDependsTasks = new ArrayList<>(1);

        for (AsyncTask task : downTasksMap.keySet()) {
            List<AsyncTask> dTasks = downTasksMap.get(task);
            if (dTasks.isEmpty()) {
                noDependsTasks.add(task);
                downTasksMap.get(task).add(starter);
            }
        }
        downTasksMap.put(starter, new ArrayList<>(0));
        upwardTasksMap.put(starter, noDependsTasks);

        for (AsyncTask task : downTasksMap.keySet()) {
            TaskActuator process;
            if (task != starter) {
                /**
                 * Each task is executed using a new Processs
                 */
                process = new TaskActuator(task, upwardTasksMap.get(task).size(), downTasksMap.get(task));
            } else {
                /***
                 * completely  and  Termination of the task
                 */
                process = new TerminationTask(task, upwardTasksMap.get(task).size(), downTasksMap.get(task));
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
        IdentityHashMap<AsyncTask, TaskActuator> newProcessMap = new IdentityHashMap<>(prepareTaskMap.size());
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
            TaskActuator newProcess = (TaskActuator) prepareTaskMap.get(task).clone();
            newProcess.init(support, param);
            newProcessMap.put(task, newProcess);
        }
        return loader;
    }


    /**
     * ScriptEndEventHandler will do work to complete the whole ScriptRuntime
     * process.
     */
    private class TerminationTask extends TaskActuator {

        TerminationTask(AsyncTask handler, int depdending, List<AsyncTask> dependedTasks) {
            super(handler, depdending, dependedTasks);
        }
        @Override
        public void run() {
            support.taskLoader.completed();
        }
    }

    /**
     * Count the number of dependent tasks by this class
     * @param <P>
     * @param <R>
     */
    private class AssistantTask<P, R> implements AsyncTask<P, R> {

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

    /**
     * Get the task Bus
     * @param param
     * @return
     */
    private TaskSupport getSupport(AsyncParam param) {
        TaskSupport taskSupport = new TaskSupport();
        taskSupport.setParam(param);
        return taskSupport;
    }

}
