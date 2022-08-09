package com.gobrs.async;

import com.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.gobrs.async.task.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Task flow.
 *
 * @program: gobrs -async-starter
 * @ClassName Task bus Responsible for task handling flow
 * @description: Task flow
 * @author: sizegang
 * @create: 2022 -03-16
 */
public class TaskFlow {


    private GobrsAsyncProperties gobrsAsyncProperties;

    /**
     * Task tree
     */
    private final IdentityHashMap<AsyncTask, List<AsyncTask>> denpendedTasks = new IdentityHashMap<>();

    /**
     * After task receive.
     *
     * @param asyncTasks the async tasks
     * @return the task receive
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
     * Start task receive.
     *
     * @param asyncTasks the async tasks
     * @return the task receive
     */
    synchronized TaskReceive start(AsyncTask... asyncTasks) {
        /**
         * Building task groups
         */
        TaskReceive builder = new TaskReceive(this, Arrays.asList(asyncTasks));
        return builder;
    }

    /**
     * Start task receive.
     *
     * @param asyncTasks the async tasks
     * @return the task receive
     */
    synchronized TaskReceive start(List<AsyncTask> asyncTasks) {
        /**
         * Building task groups
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

    /**
     * Gets gobrs async properties.
     *
     * @return the gobrs async properties
     */
    public GobrsAsyncProperties getGobrsAsyncProperties() {
        return gobrsAsyncProperties;
    }

    /**
     * Sets gobrs async properties.
     *
     * @param gobrsAsyncProperties the gobrs async properties
     */
    public void setGobrsAsyncProperties(GobrsAsyncProperties gobrsAsyncProperties) {
        this.gobrsAsyncProperties = gobrsAsyncProperties;
    }
}
