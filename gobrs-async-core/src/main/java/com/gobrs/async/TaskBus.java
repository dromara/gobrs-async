package com.gobrs.async;

import com.gobrs.async.task.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: gobrs-async-starter
 * @ClassName Task bus Responsible for task handling flow flow
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/


public class TaskBus {


    private boolean ready = false;
    /**
     * denpende Task tree
     */
    private final IdentityHashMap<AsyncTask, List<AsyncTask>> denpendedTasks = new IdentityHashMap<>();

    synchronized TaskCluster after(final AsyncTask... handlers) {
        if (ready) {
            throw new IllegalStateException(
                    "script is ready, cannot be edit any more.");
        }
        for (AsyncTask handler : handlers) {
            if (!denpendedTasks.containsKey(handler)) {
                throw new IllegalStateException(
                        "event handler is not in script yet.");
            }
        }
        return start(handlers);
    }

    synchronized TaskCluster start(AsyncTask... asyncTasks) {
        if (ready) {
            throw new IllegalStateException(
                    "script is ready, cannot be edit any more.");
        }
        /**
         * Building task groups
         */
        TaskCluster cluster = new TaskCluster(this, Arrays.asList(asyncTasks));
        return cluster;
    }

    synchronized Map<AsyncTask, List<AsyncTask>> getdenpendedEventHandlers() {
        return denpendedTasks;
    }

    synchronized void ready() {
        ready = true;
    }

    synchronized boolean isReady() {
        return ready;
    }

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
