package com.gobrs.async.core;

import com.gobrs.async.core.common.domain.AsyncParam;
import com.gobrs.async.core.common.domain.AsyncResult;
import com.gobrs.async.core.config.ConfigManager;
import com.gobrs.async.core.task.AsyncTask;
import com.gobrs.async.core.common.exception.NotTaskRuleException;

import java.util.*;

/**
 * The type Gobrs async.
 *
 * @program: gobrs -async-starter
 * @ClassName gobrs -Async
 * @description: com.gobrs.async.com.gobrs.async.test.task process executor
 * @author: sizegang
 * @create: 2022 -03-16
 */
public class GobrsAsync {


    /**
     * com.gobrs.async.com.gobrs.async.test.task process pipeline A com.gobrs.async.rule corresponds to a taskFlow
     */
    private Map<String, TaskFlow> taskFlow;

    /**
     * Task trigger wrapper
     */
    private Map<String, TaskTrigger> trigger;


    /**
     * Begin com.gobrs.async.com.gobrs.async.test.task receive.
     *
     * @param taskName the com.gobrs.async.com.gobrs.async.test.task name
     * @param tasks    the tasks
     * @return the com.gobrs.async.com.gobrs.async.test.task receive
     */
    public TaskReceive begin(String taskName, AsyncTask... tasks) {
        return taskFlow.get(taskName).start(tasks);
    }

    /**
     * Start building the com.gobrs.async.com.gobrs.async.test.task process
     *
     * @param ruleName   the com.gobrs.async.rule name
     * @param asyncTasks the async tasks
     * @param reload     the reload
     * @return com.gobrs.async.com.gobrs.async.test.task receive
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


    /**
     * Begin com.gobrs.async.com.gobrs.async.test.task receive.
     *
     * @param ruleName   the com.gobrs.async.rule name
     * @param asyncTasks the async tasks
     * @return the com.gobrs.async.com.gobrs.async.test.task receive
     */
    public TaskReceive begin(String ruleName, List<AsyncTask> asyncTasks) {
        return begin(ruleName, asyncTasks, false);
    }

    /**
     * Add subtask process
     *
     * @param taskName the com.gobrs.async.com.gobrs.async.test.task name
     * @param tasks    the tasks
     * @return com.gobrs.async.com.gobrs.async.test.task receive
     */
    public TaskReceive after(String taskName, AsyncTask... tasks) {
        return taskFlow.get(taskName).after(tasks);
    }

    /**
     * Really open the com.gobrs.async.com.gobrs.async.test.task flow Multi-threaded flow master switch Have fun
     *
     * @param ruleName the com.gobrs.async.rule name
     * @param param    the param
     * @param timeout  the timeout
     * @return async result
     */
    public AsyncResult go(String ruleName, AsyncParam param, long timeout) {
        return go(ruleName, param, null, timeout);
    }

    /**
     * Go async result.
     *
     * @param ruleName   the rule name
     * @param param      the param
     * @param affirTasks the affir tasks
     * @param timeout    the timeout
     * @return the async result
     */
    public AsyncResult go(String ruleName, AsyncParam param, Set<String> affirTasks, long timeout) {
        if (check(ruleName).isPresent()) {
            return trigger.get(ruleName).trigger(param, timeout, affirTasks).load();
        }
        throw new NotTaskRuleException("Gobrs Rule Name Is Error");
    }


    /**
     * Start the com.gobrs.async.com.gobrs.async.test.task process
     *
     * @param taskName the com.gobrs.async.com.gobrs.async.test.task name
     * @param param    the param
     * @return async result
     */
    public AsyncResult go(String taskName, AsyncParam param) {
        return go(taskName, param, 0L);
    }


    /**
     * Ready to.
     *
     * @param ruleName the com.gobrs.async.rule name
     */
    public synchronized void readyTo(String ruleName) {
        readyTo(ruleName, false);
    }

    /**
     * Preparing Task Process Execution
     *
     * @param ruleName the com.gobrs.async.rule name
     * @param reload   the reload
     */
    public synchronized void readyTo(String ruleName, boolean reload) {
        /**
         * Initialize com.gobrs.async.com.gobrs.async.test.task trigger
         */
        if (trigger == null) {
            initializeTrigger(ruleName);
        }
        /**
         * Load com.gobrs.async.com.gobrs.async.test.task trigger
         */
        if (trigger.get(ruleName) == null) {
            loadTrigger(ruleName);
        }
        if (reload) {
            loadTrigger(ruleName);
        }
    }


    /**
     * Load com.gobrs.async.com.gobrs.async.test.task flow
     *
     * @param ruleName
     */
    private void loadTaskFlow(String ruleName) {
        taskFlow = new HashMap<>();
        TaskFlow tf = new TaskFlow();
        taskFlow.put(ruleName, tf);
    }

    /**
     * Load the first com.gobrs.async.com.gobrs.async.test.task process
     *
     * @param ruleName
     */
    private void loadTaskFlowForOne(String ruleName) {
        TaskFlow tf = new TaskFlow();
        taskFlow.put(ruleName, tf);
    }

    /**
     * Load com.gobrs.async.com.gobrs.async.test.task trigger
     *
     * @param ruleName
     */
    private void initializeTrigger(String ruleName) {
        trigger = new HashMap<>();
        TaskTrigger tr = new TaskTrigger(ruleName, taskFlow.get(ruleName));
        trigger.put(ruleName, tr);
    }

    /**
     * Create your first com.gobrs.async.com.gobrs.async.test.task trigger
     *
     * @param ruleName
     */
    private void loadTrigger(String ruleName) {
        TaskTrigger tr = new TaskTrigger(ruleName, taskFlow.get(ruleName));
        trigger.put(ruleName, tr);
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

