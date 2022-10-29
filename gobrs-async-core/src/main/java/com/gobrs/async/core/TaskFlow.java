package com.gobrs.async.core;

import com.gobrs.async.core.task.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Task flow.
 *
 * @program: gobrs -async-starter
 * @ClassName Task bus Responsible for com.gobrs.async.com.gobrs.async.test.task handling flow
 * @description: Task flow
 * @author: sizegang
 * @create: 2022 -03-16
 */
public class TaskFlow {

    /**
     * Task tree
     */
    private final IdentityHashMap<AsyncTask, List<AsyncTask>> denpendedTasks = new IdentityHashMap<>();

    /**
     * After com.gobrs.async.com.gobrs.async.test.task receive.
     *
     * @param asyncTasks the async tasks
     * @return the com.gobrs.async.com.gobrs.async.test.task receive
     */
    synchronized TaskReceive after(final AsyncTask... asyncTasks) {

        for (AsyncTask handler : asyncTasks) {
            if (!denpendedTasks.containsKey(handler)) {
                throw new IllegalStateException(
                        "asyncTask not begin command");
            }
        }
        return start(asyncTasks);
    }

    /**
     * Start com.gobrs.async.com.gobrs.async.test.task receive.
     *
     * @param asyncTasks the async tasks
     * @return the com.gobrs.async.com.gobrs.async.test.task receive
     */
    synchronized TaskReceive start(AsyncTask... asyncTasks) {
        /**
         * Building com.gobrs.async.com.gobrs.async.test.task groups
         */
        TaskReceive builder = new TaskReceive(this, Arrays.asList(asyncTasks));
        return builder;
    }

    /**
     * Start com.gobrs.async.com.gobrs.async.test.task receive.
     *
     * @param asyncTasks the async tasks
     * @return the com.gobrs.async.com.gobrs.async.test.task receive
     */
    synchronized TaskReceive start(List<AsyncTask> asyncTasks) {
        /**
         * Building com.gobrs.async.com.gobrs.async.test.task groups
         */
        TaskReceive builder = new TaskReceive(this, asyncTasks);
        return builder;
    }

    /**
     * Gets depends tasks.
     *
     * @return the depends tasks
     */
    synchronized Map<AsyncTask, List<AsyncTask>> getDependsTasks() {
        return denpendedTasks;
    }


    /**
     * Add dependency.
     *
     * @param from the from
     * @param to   the to
     */
    void addDependency(AsyncTask from, AsyncTask to) {
        // create
        if (!denpendedTasks.containsKey(from)) {
            denpendedTasks.put(from, new ArrayList<>(0));
        }
        //There is add
        if (to != null && !denpendedTasks.get(from).contains(to)) {
            denpendedTasks.get(from).add(to);
        }
    }
}
