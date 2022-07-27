package com.gobrs.async;

import com.gobrs.async.domain.AsyncParam;
import com.gobrs.async.spring.GobrsSpring;
import com.gobrs.async.task.AsyncTask;
import com.gobrs.async.task.Task;
import com.gobrs.async.threadpool.GobrsAsyncThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * The type Task trigger.
 *
 * @program: gobrs -async-starter
 * @description: Task preloader
 * @author: sizegang
 * @create: 2022 -03-16
 */
class TaskTrigger {

    /**
     * The Logger.
     */
    Logger logger = LoggerFactory.getLogger(TaskTrigger.class);
    private final TaskFlow taskFlow;

    private GobrsAsyncThreadPoolFactory threadPoolFactory = GobrsSpring.getBean(GobrsAsyncThreadPoolFactory.class);

    private Map<AsyncTask, TaskActuator> prepareTaskMap = Collections.synchronizedMap(new IdentityHashMap<>());

    /**
     * The Assistant task.
     */
    public AssistantTask assistantTask;

    /**
     * The Upward tasks map space.
     */
    public static Map<AsyncTask, List<AsyncTask>> upwardTasksMapSpace = new HashMap<>();

    /**
     * Instantiates a new Task trigger.
     *
     * @param taskFlow the task flow
     */
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
        upwardTasksMapSpace = upwardTasksMap;
        for (AsyncTask task : downTasksMap.keySet()) {
            TaskActuator process;
            if (task != assistantTask) {
                /**
                 * Each business task is executed using a new taskActuator
                 */
                process = new TaskActuator(task, upwardTasksMap.get(task).size(), downTasksMap.get(task), upwardTasksMap);
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


    /**
     * Trigger task loader.
     *
     * @param param   the param
     * @param timeout the timeout
     * @return the task loader
     */
    TaskLoader trigger(AsyncParam param, long timeout) {
        return trigger(param, timeout, null);
    }

    /**
     * Trigger task loader.
     *
     * @param param      the param
     * @param affirTasks the affir tasks
     * @param timeout    the timeout
     * @return the task loader
     */
    TaskLoader trigger(AsyncParam param, Set<String> affirTasks, long timeout) {
        return trigger(param, timeout, affirTasks);
    }

    /**
     * Trigger task loader.
     *
     * @param param      the param
     * @param timeout    the timeout
     * @param affirTasks the affir tasks
     * @return the task loader
     */
    TaskLoader trigger(AsyncParam param, long timeout, Set<String> affirTasks) {
        IdentityHashMap<AsyncTask, TaskActuator> newProcessMap = new IdentityHashMap<>(prepareTaskMap.size());
        /**
         * Create a task loader, A task flow corresponds to a taskLoader
         */
        TaskLoader loader = new TaskLoader(threadPoolFactory.getThreadPoolExecutor(), newProcessMap, timeout);
        TaskSupport support = getSupport(param);
        loader.setAssistantTask(assistantTask);

        support.setTaskLoader(loader);
        /**
         * The thread pool is obtained from the factory, and the thread pool parameters can be dynamically adjusted
         */
        support.setExecutorService(threadPoolFactory.getThreadPoolExecutor());

        for (AsyncTask task : prepareTaskMap.keySet()) {
            /**
             * clone Process for Thread isolation
             */
            TaskActuator processor = (TaskActuator) prepareTaskMap.get(task).clone();
            processor.init(support, param);
            newProcessMap.put(task, processor);
        }
        Optimal.doOptimal(affirTasks, loader, upwardTasksMapSpace);
        return loader;
    }

    /**
     * Task flow End tasks
     */
    private class TerminationTask extends TaskActuator {

        /**
         * task executor
         *
         * @param handler       the handler
         * @param depdending    The number of tasks to depend on
         * @param dependedTasks Array of dependent tasks
         */
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
     * @param <P> the type parameter
     * @param <R> the type parameter
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


    private void doProcess(TaskLoader taskLoader, TaskActuator process, Set<AsyncTask> affirTasks) {
        if (affirTasks != null) {
            if (affirTasks.contains(process.getTask())) {
                taskLoader.affirCount.incrementAndGet();
                taskLoader.startProcess(process);
            }
        } else {
            taskLoader.startProcess(process);
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
