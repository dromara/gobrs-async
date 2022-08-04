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
     * @param affirTasks the affir tasks
     * @param taskLoader the task loader
     * @param process    the process
     * @return the boolean
     */
    public static boolean ifContinue(Set<AsyncTask> affirTasks, TaskLoader taskLoader, TaskActuator process) {
        if (affirTasks != null && taskLoader.affirCount.get() == taskLoader.getAffirTasks().size()) {
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
        if (taskLoader.getAffirTasks() != null) {
            taskLoader.affirCount.incrementAndGet();
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
        if (affirTasks != null) {
            processMap.get(assistantTask).setUpstreamDepdends(affirTasks.size());
        }
    }


    /**
     * Do optimal.
     *
     * @param affirTasks          the affir tasks
     * @param loader              the loader
     * @param upwardTasksMapSpace the upward tasks map space
     */
    public static void doOptimal(Set<String> affirTasks, TaskLoader loader, Map<AsyncTask, List<AsyncTask>> upwardTasksMapSpace) {
        Set<AsyncTask> asyncTaskSet = new HashSet<>();
        if (affirTasks == null) {
            return;
        }
        for (String x : affirTasks) {
            Object bean = GobrsSpring.getBean(x);
            if (bean == null) {
                logger.error("【Gobrs-Async print】affir Task name empty {}", x);
                continue;
            }
            AsyncTask task = (AsyncTask) bean;
            List<AsyncTask> asyncTasks = upwardTasksMapSpace.get(task);
            if (Objects.isNull(asyncTasks)) {
                throw new GobrsAsyncException(String.format("gobrs-rule config exception, task %s is nessary", task.getName()));
            }
            asyncTasks.add(task);
            asyncTaskSet.addAll(new HashSet<>(asyncTasks));
        }
        loader.setAffirTasks(asyncTaskSet);
    }


}