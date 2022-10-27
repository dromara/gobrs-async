package com.gobrs.async;

/**
 * @program: gobrs-async
 * @ClassName Affir
 * @description:
 * @author: sizegang
 * @create: 2022-07-23
 **/

import com.gobrs.async.exception.GobrsAsyncException;
import com.gobrs.async.spring.GobrsSpring;
import com.gobrs.async.task.AsyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * The type Optimal.
 */
public class Optimal {
    /**
     * The Logger.
     */
    static Logger logger = LoggerFactory.getLogger(Optimal.class);

    /**
     * If continue boolean.
     *
     * @param optionalTasks the optional tasks
     * @param taskLoader    the task loader
     * @param process       the process
     * @return the boolean
     */
    public static boolean ifContinue(Set<AsyncTask> optionalTasks, TaskLoader taskLoader, TaskActuator process) {
        if (optionalTasks != null && taskLoader.oplCount.get() == taskLoader.getOptionalTasks().size()) {
            taskLoader.processMap.get(taskLoader.assistantTask).run();
            return false;
        }
        return true;
    }

    /**
     * Optimal count.
     *
     * @param taskLoader the task loader
     */
    public static void optimalCount(TaskLoader taskLoader) {
        if (Objects.nonNull(taskLoader.getOptionalTasks())) {
            taskLoader.oplCount.incrementAndGet();
        }
    }

    /**
     * ifOptimal
     *
     * @param affirTasks    the affir tasks
     * @param processMap    the process map
     * @param assistantTask the assistant task
     */
    public static void ifOptimal(Set<AsyncTask> affirTasks, Map<AsyncTask, TaskActuator> processMap, TaskTrigger.AssistantTask assistantTask) {
        processMap.get(assistantTask).setUpstreamDepdends(affirTasks.size());
    }


    /**
     * Do optimal.
     *
     * @param optionalTasks       the optional tasks
     * @param loader              the loader
     * @param upwardTasksMapSpace the upward tasks map space
     */
    public static void doOptimal(Set<String> optionalTasks, TaskLoader loader, Map<AsyncTask, List<AsyncTask>> upwardTasksMapSpace) {

        Set<AsyncTask> asyncTaskSet = new HashSet<>();

        if (CollectionUtils.isEmpty(optionalTasks)) {
            return;
        }

        for (String x : optionalTasks) {
            Object bean = GobrsSpring.getBean(x);
            if (bean == null) {
                logger.error("【Gobrs-Async print】affir Task name empty {}", x);
                continue;
            }

            if (!(bean instanceof AsyncTask)) {
                continue;
            }

            AsyncTask task = (AsyncTask) bean;

            recursionUpward(upwardTasksMapSpace, task, asyncTaskSet);

            if (Objects.isNull(asyncTaskSet)) {
                throw new GobrsAsyncException(String.format("task %s in  springboot yaml or properties must exist", task.getClass().getSimpleName()));
            }

            asyncTaskSet.add(task);
        }
        loader.setOptionalTasks(asyncTaskSet);
    }

    private static void recursionUpward(Map<AsyncTask, List<AsyncTask>> upwardTasksMapSpace, AsyncTask task, Set<AsyncTask> allTask) {

        List<AsyncTask> asyncTasks = upwardTasksMapSpace.get(task);

        if (CollectionUtils.isEmpty(asyncTasks)) {
            return;
        }

        allTask.addAll(asyncTasks);

        for (AsyncTask asyncTask : asyncTasks) {
            recursionUpward(upwardTasksMapSpace, asyncTask, allTask);
        }
    }


}