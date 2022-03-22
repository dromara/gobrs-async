package com.gobrs.async;

import com.gobrs.async.task.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @program: gobrs-async-starter
 * @ClassName
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/
public class TaskRecevie {

    private final TaskFlow taskFlow;
    /**
     * cache taskList
     */
    private List<AsyncTask> cacheTaskList;

    TaskRecevie(TaskFlow taskFlow, List<AsyncTask> taskList) {
        synchronized (taskFlow) {
            this.taskFlow = taskFlow;
            this.cacheTaskList = new ArrayList<>(taskList.size());
            /**
             *  src -> dest
             */
            copyList(taskList, this.cacheTaskList);
            for (AsyncTask task : taskList) {
                taskFlow.addDependency(task, null);
            }
        }
    }

    public TaskRecevie then(AsyncTask... eventHandlers) {
        synchronized (taskFlow) {
            for (AsyncTask from : this.cacheTaskList) {
                for (AsyncTask to : eventHandlers) {
                    taskFlow.addDependency(from, to);
                }
            }
            for (AsyncTask to : eventHandlers) {
                taskFlow.addDependency(to, null);
            }
            this.cacheTaskList = new ArrayList<AsyncTask>(
                    eventHandlers.length);
            copyList(Arrays.asList(eventHandlers), this.cacheTaskList);
            return this;
        }
    }

    private void copyList(List<AsyncTask> src,
                          List<AsyncTask> dest) {
        for (AsyncTask s : src) {
            dest.add(s);
        }
    }

}
