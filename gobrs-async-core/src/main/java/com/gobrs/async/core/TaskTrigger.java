package com.gobrs.async.core;

import com.gobrs.async.core.common.domain.AsyncParam;
import com.gobrs.async.core.common.util.SystemClock;
import com.gobrs.async.core.config.ConfigManager;
import com.gobrs.async.core.holder.BeanHolder;
import com.gobrs.async.core.log.LogWrapper;
import com.gobrs.async.core.task.AsyncTask;
import com.gobrs.async.core.threadpool.GobrsAsyncThreadPoolFactory;
import com.gobrs.async.core.common.util.IdWorker;
import com.gobrs.async.core.common.util.JsonUtil;
import com.gobrs.async.core.log.TraceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The type Task trigger.
 *
 * @param <P> the type parameter
 * @param <R> the type parameter
 * @program: gobrs -async-starter
 * @description: Task preloader
 * @author: sizegang
 * @create: 2022 -03-16
 */
class TaskTrigger<P, R> {

    /**
     * The Logger.
     */
    Logger logger = LoggerFactory.getLogger(TaskTrigger.class);

    private final TaskFlow taskFlow;

    private GobrsAsyncThreadPoolFactory threadPoolFactory = BeanHolder.getBean(GobrsAsyncThreadPoolFactory.class);

    private Map<AsyncTask, TaskActuator> prepareTaskMap = Collections.synchronizedMap(new IdentityHashMap<>());

    private Map<AsyncTask, TaskActuator> prepareTaskMapWrite = new IdentityHashMap<>();

    private String ruleName;

    /**
     * The Assistant com.gobrs.async.com.gobrs.async.test.task.
     */
    public AssistantTask assistantTask;

    /**
     * The Upward tasks map space.
     */
    public static Map<String, Map<AsyncTask, List<AsyncTask>>> upwardTasksMapSpace = new ConcurrentHashMap<>();

    /**
     * Instantiates a new Task trigger.
     *
     * @param ruleName the rule name
     * @param taskFlow the com.gobrs.async.com.gobrs.async.test.task flow
     */
    TaskTrigger(String ruleName, TaskFlow taskFlow) {
        this.ruleName = ruleName;
        this.taskFlow = taskFlow;
        prepare();
    }


    /**
     * Build com.gobrs.async.com.gobrs.async.test.task dependencies Load the cache for the first time when a project is started, subsequent cache processing is performed only once
     * 预加载任务依赖关系
     * 缓存依赖任务配置、维护 任务所依赖的任务数量和任务对象
     */
    private void prepare() {


        /**
         * Subtasks under a com.gobrs.async.com.gobrs.async.test.task
         */
        Map<AsyncTask, List<AsyncTask>> downTasksMap = copyDependTasks(taskFlow.getDependsTasks());

        /**
         * The com.gobrs.async.com.gobrs.async.test.task on which a com.gobrs.async.com.gobrs.async.test.task depends
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
         * com.gobrs.async.com.gobrs.async.test.task without any subtasks
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
        upwardTasksMapSpace.put(ruleName,upwardTasksMap);
        clear();
        for (AsyncTask task : downTasksMap.keySet()) {
            TaskActuator process;
            if (task != assistantTask) {
                List<AsyncTask> circularDependency = upwardTasksMap.get(task).stream()
                        .filter(x -> x.getName().equals(task.getName())).collect(Collectors.toList());
                int upDepend = 0;
                if (circularDependency.size() == 0) {
                    upDepend = upwardTasksMap.get(task).size();
                }
                /**
                 * Each business com.gobrs.async.com.gobrs.async.test.task is executed using a new taskActuator
                 */
                process = new TaskActuator(task, upDepend, downTasksMap.get(task), upwardTasksMap);
            } else {
                /***
                 * completely  and  Termination of the com.gobrs.async.com.gobrs.async.test.task
                 */
                process = new TerminationTask(task, upwardTasksMap.get(task).size(), downTasksMap.get(task));
            }
            prepareTaskMapWrite.put(task, process);
        }
        /**
         * 读写分离
         */
        prepareTaskMap = prepareTaskMapWrite;
        if (logger.isInfoEnabled()) {
            logger.info("prepareTaskMap build success {}", JsonUtil.obj2String(prepareTaskMap));
        }
    }

    /**
     * 清空写配置缓存
     */
    private void clear() {
        prepareTaskMapWrite.clear();
    }

    private Map<AsyncTask, List<AsyncTask>> copyDependTasks(Map<AsyncTask, List<AsyncTask>> handlerMap) {
        IdentityHashMap<AsyncTask, List<AsyncTask>> rt = new IdentityHashMap<>();
        for (AsyncTask asyncTask : handlerMap.keySet()) {
            rt.put(asyncTask, new ArrayList<>(handlerMap.get(asyncTask)));
        }
        return rt;
    }


    /**
     * Trigger com.gobrs.async.com.gobrs.async.test.task loader.
     *
     * @param param   the param
     * @param timeout the timeout
     * @return the com.gobrs.async.com.gobrs.async.test.task loader
     */
    TaskLoader trigger(AsyncParam param, long timeout) {
        return trigger(param, timeout, null);
    }

    /**
     * Trigger com.gobrs.async.com.gobrs.async.test.task loader.
     *
     * @param param      the param
     * @param affirTasks the affir tasks
     * @param timeout    the timeout
     * @return the com.gobrs.async.com.gobrs.async.test.task loader
     */
    TaskLoader trigger(AsyncParam param, Set<String> affirTasks, long timeout) {
        return trigger(param, timeout, affirTasks);
    }

    /**
     * Trigger com.gobrs.async.com.gobrs.async.test.task loader.
     * 触发任务加载 环境准备
     * 链路日志
     * 线程池配置
     * 从缓存好的预加载配置中 clone副本 浅clone 只备份基本类型
     *
     * @param param         the param
     * @param timeout       the timeout
     * @param optionalTasks the optional tasks
     * @return the com.gobrs.async.com.gobrs.async.test.task loader
     */
    TaskLoader trigger(AsyncParam<P> param, long timeout, Set<String> optionalTasks) {

        IdentityHashMap<AsyncTask, TaskActuator> newProcessMap = new IdentityHashMap<>(prepareTaskMap.size());
        /**
         * Create a com.gobrs.async.com.gobrs.async.test.task loader, A com.gobrs.async.com.gobrs.async.test.task flow corresponds to a taskLoader
         */
        TaskLoader<P, R> loader = new TaskLoader<>(ruleName, threadPoolFactory.getThreadPoolExecutor(), newProcessMap, timeout);

        TaskSupport support = related(param, loader);

        for (AsyncTask task : prepareTaskMap.keySet()) {
            /**
             * clone Process for Thread isolation
             */
            TaskActuator processor = (TaskActuator<?>) prepareTaskMap.get(task).clone();

            processor.init(support, param);

            newProcessMap.put(task, processor);
        }

        Optimal.doOptimal(optionalTasks, loader, upwardTasksMapSpace.get(ruleName));

        return loader;
    }

    /**
     * 设置 任务总线和任务加载器关联关系
     * 配置设置 环境加载
     *
     * @param param
     * @param loader
     * @return
     */
    private TaskSupport related(AsyncParam<P> param, TaskLoader<P, R> loader) {

        TaskSupport support = getSupport(param);

        support.setTaskLoader(loader);

        logAdvance(support);

        loader.setAssistantTask(assistantTask);

        /**
         * The thread pool is obtained from the factory, and the thread pool parameters can be dynamically adjusted
         */
        support.setExecutorService(threadPoolFactory.getThreadPoolExecutor());
        return support;
    }


    /**
     * 终止任务 在整个任务流程结束后 会调用该任务类执行 completed()
     * Task flow End tasks
     */
    private class TerminationTask<P, R> extends TaskActuator<Object> {

        /**
         * com.gobrs.async.com.gobrs.async.test.task executor
         *
         * @param handler       the handler
         * @param depdending    The number of tasks to depend on
         * @param dependedTasks Array of dependent tasks
         */
        TerminationTask(AsyncTask<P, R> handler, int depdending, List<AsyncTask> dependedTasks) {
            super(handler, depdending, dependedTasks);
        }

        /**
         * Task completion interrupt the main thread blocks
         */
        @Override
        public Object call() {
            support.taskLoader.completed();
            return null;
        }
    }

    /**
     * Assistant com.gobrs.async.com.gobrs.async.test.task, help the com.gobrs.async.com.gobrs.async.test.task process to finish properly
     * 助理任务 通过助理任务可以协助组装任务依赖配置
     *
     * @param <P> the type parameter
     * @param <R> the type parameter
     */
    public class AssistantTask<P, R> extends AsyncTask<P, R> {
        @Override
        public R task(P p, TaskSupport support) {
            return null;
        }
    }


    private void doProcess(TaskLoader taskLoader, TaskActuator process, Set<AsyncTask> affirTasks) {
        if (affirTasks != null) {
            if (affirTasks.contains(process.getTask())) {
                taskLoader.oplCount.incrementAndGet();
                taskLoader.startProcess(process);
            }
        } else {
            taskLoader.startProcess(process);
        }
    }


    /**
     * Get the com.gobrs.async.com.gobrs.async.test.task support , Similar com.gobrs.async.com.gobrs.async.test.task bus
     * 获取任务流程 总线
     * support 封装流程中重要的 配置信息和任务执行过程中的流转信息
     *
     * @param param
     * @return
     */
    private TaskSupport getSupport(AsyncParam param) {
        return new TaskSupport().
                setParam(param)
                .setRuleName(ruleName);
    }


    /**
     * 1、traceId
     * 2、日志
     *
     * @param support
     */
    private void logAdvance(TaskSupport support) {

        long traceId = IdWorker.nextId();
        TraceUtil.set(traceId);
        boolean costLogabled = ConfigManager.Action.costLogabled(ruleName);
        if (costLogabled) {
            LogWrapper.TimeCollector timeCollector =
                    LogWrapper.TimeCollector.builder()
                            .startTime(SystemClock.now())
                            .build();
            LogWrapper logWrapper = new LogWrapper()
                    .setTraceId(traceId)
                    .setTimeCollector(timeCollector);
            support.setLogWrapper(logWrapper);
            support.getTaskLoader().setLogWrapper(logWrapper);
        }

    }

    /**
     * @param rulename
     */
    private void rule(String rulename) {
        this.ruleName = rulename;
    }
}
