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

    private GobrsAsyncThreadPoolFactory threadPoolFactory = GobrsSpring.getBean(GobrsAsyncThreadPoolFactory.class);

    private IdentityHashMap<AsyncTask, TaskActuator> prepareTaskMap = new IdentityHashMap<>();

    public AssistantTask assistantTask;

    TaskTrigger(TaskFlow taskFlow) {
        this.taskFlow = taskFlow;
        prepare();
    }

    /**
     * Build task dependencies Load the cache for the first time when a project is started, subsequent cache processing is performed only once
     */
    private void prepare() {

        /**
         * Subtasks under a task
         */
        Map<AsyncTask, List<AsyncTask>> downTasksMap = copyDependTasks(taskFlow.getDependsTasks());

        /**
         * The task on which a task depends
         */
        Map<AsyncTask, List<AsyncTask>> upwardTasksMap = new HashMap<>();

        for (AsyncTask task : downTasksMap.keySet()) {
            upwardTasksMap.put(task, new ArrayList<>(1));
        }


        for (AsyncTask task : downTasksMap.keySet()) {
            for (AsyncTask depended : downTasksMap.get(task)) {
                upwardTasksMap.get(depended).add(task);
            }
        }


        assistantTask = new AssistantTask();
        /**
         * task without any subtasks
         */
        List<AsyncTask> noSubtasks = new ArrayList<>(1);

        for (AsyncTask task : downTasksMap.keySet()) {
            List<AsyncTask> dTasks = downTasksMap.get(task);
            if (dTasks.isEmpty()) {
                noSubtasks.add(task);
                downTasksMap.get(task).add(assistantTask);
            }
        }
        downTasksMap.put(assistantTask, new ArrayList<>(0));
        upwardTasksMap.put(assistantTask, noSubtasks);

        for (AsyncTask task : downTasksMap.keySet()) {
            TaskActuator process;
            if (task != assistantTask) {
                /**
                 * Each business task is executed using a new taskActuator
                 */
                if (taskFlow.getGobrsAsyncProperties().isTransaction()) {
                    process = new TaskActuator(task, upwardTasksMap.get(task).size(), downTasksMap.get(task), upwardTasksMap);
                } else {
                    process = new TaskActuator(task, upwardTasksMap.get(task).size(), downTasksMap.get(task));
                }
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
        for (AsyncTask asyncTask : handlerMap.keySet()) {
            rt.put(asyncTask, new ArrayList<>(handlerMap.get(asyncTask)));
        }
        return rt;
    }


    TaskLoader trigger(AsyncParam param, long timeout) {
        IdentityHashMap<AsyncTask, TaskActuator> newProcessMap = new IdentityHashMap<>(prepareTaskMap.size());
        /**
         * Create a task loader, A task flow corresponds to a taskLoader
         */
        TaskLoader loader = new TaskLoader(threadPoolFactory.getThreadPoolExecutor(), newProcessMap, timeout);
        TaskSupport support = getSupport(param);
        loader.setAssistantTask(assistantTask);
        support.setTaskLoader(loader);
        support.setExecutorService(threadPoolFactory.getThreadPoolExecutor());
        for (AsyncTask task : prepareTaskMap.keySet()) {
            /**
             * clone Process for Thread isolation
             */
            TaskActuator newProcess = (TaskActuator) prepareTaskMap.get(task).clone();
            newProcess.init(support, param);
            newProcessMap.put(task, newProcess);
        }
        return loader;
    }


    /**
     * Task flow End tasks
     */
    private class TerminationTask extends TaskActuator {

        TerminationTask(AsyncTask handler, int depdending, List<AsyncTask> dependedTasks) {
            super(handler, depdending, dependedTasks);
        }

        /**
         * Task completion interrupt the main thread blocks
         */
        @Override
        public void run() {
            support.taskLoader.completed();
        }
    }

    /**
     * Assistant task, help the task process to finish properly
     *
     * @param <P>
     * @param <R>
     */
    public class AssistantTask<P, R> extends AsyncTask<P, R> {

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
     * Get the task support , Similar task bus
     *
     * @param param
     * @return
     */
    private TaskSupport getSupport(AsyncParam param) {
        TaskSupport taskSupport = new TaskSupport();
        taskSupport.setParam(param.get());
        return taskSupport;
    }

}
