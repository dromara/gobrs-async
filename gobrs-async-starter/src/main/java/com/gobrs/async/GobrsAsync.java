package com.gobrs.async;

import com.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.gobrs.async.domain.AsyncParam;
import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.exception.NotTaskRuleException;
import com.gobrs.async.task.AsyncTask;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @program: gobrs-async-starter
 * @ClassName gobrs-Async
 * @description: task process executor
 * @author: sizegang
 * @create: 2022-03-16
 **/
public class GobrsAsync {


    @Autowired
    private GobrsAsyncProperties gobrsAsyncProperties;

    /**
     * task process pipeline A rule corresponds to a taskFlow
     */
    private Map<String, TaskFlow> taskFlow;

    /**
     * Task trigger wrapper
     */
    private Map<String, TaskTrigger> trigger;


    public TaskReceive begin(String taskName, AsyncTask... tasks) {
        return taskFlow.get(taskName).start(tasks);
    }

    /**
     * Start building the task process
     *
     * @param ruleName
     * @param asyncTasks
     * @param reload
     * @return
     */
    public TaskReceive begin(String ruleName, List<AsyncTask> asyncTasks, boolean reload) {
        if (taskFlow == null) {
            loadTaskFlow(ruleName);
        }
        if (taskFlow.get(ruleName) == null) {
            loadTaskFlowForOne(ruleName);
        }
        if (reload) {
            loadTaskFlowForOne(ruleName);
        }
        return taskFlow.get(ruleName).start(asyncTasks);
    }


    public TaskReceive begin(String ruleName, List<AsyncTask> asyncTasks) {
        return begin(ruleName, asyncTasks, false);
    }

    /**
     * Add subtask process
     *
     * @param taskName
     * @param tasks
     * @return
     */
    public TaskReceive after(String taskName, AsyncTask... tasks) {
        return taskFlow.get(taskName).after(tasks);
    }

    /**
     * Really open the task flow Multi-threaded flow master switch Have fun
     *
     * @param ruleName
     * @param param
     * @param timeout
     * @return
     */
    public AsyncResult go(String ruleName, AsyncParam param, long timeout) {
        if (check(ruleName).isPresent()) {
            return trigger.get(ruleName).trigger(param, timeout).load();
        }
        throw new NotTaskRuleException("Gobrs Rule Name Is Error");

    }

    /**
     * Start the task process
     *
     * @param taskName
     * @param param
     * @return
     */
    public AsyncResult go(String taskName, AsyncParam param) {
        return go(taskName, param, 0L);
    }


    public synchronized void readyTo(String ruleName) {
        readyTo(ruleName, false);
    }

    /**
     * Preparing Task Process Execution
     *
     * @param ruleName
     * @param reload
     */
    public synchronized void readyTo(String ruleName, boolean reload) {
        /**
         * Initialize task trigger
         */
        if (trigger == null) {
            initializeTrigger(ruleName);
        }
        /**
         * Load task trigger
         */
        if (trigger.get(ruleName) == null) {
            loadTrigger(ruleName);
        }
        if (reload) {
            loadTrigger(ruleName);
        }
    }


    /**
     * Load task flow
     *
     * @param ruleName
     */
    private void loadTaskFlow(String ruleName) {
        taskFlow = new HashMap<>();
        TaskFlow tf = new TaskFlow();
        tf.setGobrsAsyncProperties(gobrsAsyncProperties);
        taskFlow.put(ruleName, tf);
    }

    /**
     * Load the first task process
     *
     * @param ruleName
     */
    private void loadTaskFlowForOne(String ruleName) {
        TaskFlow tf = new TaskFlow();
        tf.setGobrsAsyncProperties(gobrsAsyncProperties);
        taskFlow.put(ruleName, tf);
    }

    /**
     * Load task trigger
     *
     * @param ruleName
     */
    private void initializeTrigger(String ruleName) {
        trigger = new HashMap<>();
        TaskTrigger tr = new TaskTrigger(taskFlow.get(ruleName));
        trigger.put(ruleName, tr);
    }

    /**
     * Create your first task trigger
     *
     * @param taskName
     */
    private void loadTrigger(String taskName) {
        TaskTrigger tr = new TaskTrigger(taskFlow.get(taskName));
        trigger.put(taskName, tr);
    }

    /**
     * Check Task Flow Rules
     *
     * @param ruleName
     * @return
     */
    private Optional<TaskTrigger> check(String ruleName) {
        return Optional.ofNullable(trigger.get(ruleName));
    }

}

