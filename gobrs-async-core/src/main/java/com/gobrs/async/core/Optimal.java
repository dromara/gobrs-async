package com.gobrs.async.core; /**
 * @program: gobrs-async
 * @ClassName Affir
 * @description:
 * @author: sizegang
 * @create: 2022-07-23
 **/

import com.gobrs.async.core.holder.BeanHolder;
import com.gobrs.async.core.task.AsyncTask;
import com.gobrs.async.core.common.exception.GobrsAsyncException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * The type com.gobrs.async.Optimal.
 */
@Slf4j
public class Optimal {

    /**
     * If continue boolean.
     * 可选的任务是否有资格继续执行
     *
     * @param optionalTasks the optional tasks
     * @param taskLoader    the com.gobrs.async.com.gobrs.async.test.task loader
     * @param process       the process
     * @return the boolean
     */
    public static boolean ifContinue(Set<AsyncTask> optionalTasks, TaskLoader taskLoader, TaskActuator process) {
        if (optionalTasks != null && taskLoader.oplCount.get() == taskLoader.getOptionalTasks().size()) {
            ((TaskActuator) taskLoader.processMap.get(taskLoader.assistantTask)).call();
            return false;
        }
        return true;
    }

    /**
     * com.gobrs.async.Optimal count.
     *
     * @param taskLoader the com.gobrs.async.com.gobrs.async.test.task loader
     */
    public static void optimalCount(TaskLoader taskLoader) {
        if (Objects.nonNull(taskLoader.getOptionalTasks())) {
            taskLoader.oplCount.incrementAndGet();
        }
    }

    /**
     * ifOptimal
     *
     * @param optionalTasks the optional tasks
     * @param processMap    the process map
     * @param assistantTask the assistant com.gobrs.async.com.gobrs.async.test.task
     */
    public static void ifOptimal(Set<AsyncTask> optionalTasks, Map<AsyncTask, TaskActuator> processMap, TaskTrigger.AssistantTask assistantTask) {
        processMap.get(assistantTask).setUpstreamDepdends(optionalTasks.size());
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
            Object bean = BeanHolder.getBean(x);
            if (bean == null) {
                log.error("【Gobrs-Async print】affir Task name empty {}", x);
                continue;
            }

            if (!(bean instanceof AsyncTask)) {
                continue;
            }

            AsyncTask task = (AsyncTask) bean;

            recursionUpward(upwardTasksMapSpace, task, asyncTaskSet);

            if (Objects.isNull(asyncTaskSet)) {
                throw new GobrsAsyncException(String.format("com.gobrs.async.com.gobrs.async.test.task %s in  springboot yaml or properties must exist", task.getClass().getSimpleName()));
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