package com.gobrs.async;

import com.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.gobrs.async.task.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: gobrs-async-starter
 * @ClassName Task bus Responsible for task handling flow
 * @description: Task flow
 * @author: sizegang
 * @create: 2022-03-16
 **/
public class TaskFlow {


    private GobrsAsyncProperties gobrsAsyncProperties;

    /**
     * Task tree
     */
    private final IdentityHashMap<AsyncTask, List<AsyncTask>> denpendedTasks = new IdentityHashMap<>();

    synchronized TaskReceive after(final AsyncTask... asyncTasks) {

        for (AsyncTask handler : asyncTasks) {
            if (!denpendedTasks.containsKey(handler)) {
                throw new IllegalStateException(
                        "asyncTask not begin command");
            }
        }
        return start(asyncTasks);
    }

    synchronized TaskReceive start(AsyncTask... asyncTasks) {
        /**
         * Building task groups
         */
        TaskReceive builder = new TaskReceive(this, Arrays.asList(asyncTasks));
        return builder;
    }

    synchronized TaskReceive start(List<AsyncTask> asyncTasks) {
        /**
         * Building task groups
         */
        TaskReceive builder = new TaskReceive(this, asyncTasks);
        return builder;
    }

    synchronized Map<AsyncTask, List<AsyncTask>> getDependsTasks() {
        return denpendedTasks;
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

    public GobrsAsyncProperties getGobrsAsyncProperties() {
        return gobrsAsyncProperties;
    }

    public void setGobrsAsyncProperties(GobrsAsyncProperties gobrsAsyncProperties) {
        this.gobrsAsyncProperties = gobrsAsyncProperties;
    }
}
