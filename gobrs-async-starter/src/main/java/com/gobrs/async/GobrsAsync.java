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
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/
public class GobrsAsync {


    @Autowired
    private GobrsAsyncProperties gobrsAsyncProperties;

    // A rule corresponds to a taskFlow
    private Map<String, TaskFlow> taskFlow;

    private Map<String, TaskProcess> trigger;


    public TaskRecevie begin(String taskName, AsyncTask... tasks) {
        return taskFlow.get(taskName).start(tasks);
    }


    public TaskRecevie begin(String ruleName, List<AsyncTask> asyncTasks) {
        if (taskFlow == null) {
            loadTaskFlow(ruleName);
        }
        if (taskFlow.get(ruleName) == null) {
            loadTaskFlowForOne(ruleName);
        }
        return taskFlow.get(ruleName).start(asyncTasks);
    }

    public TaskRecevie after(String taskName, AsyncTask... tasks) {
        return taskFlow.get(taskName).after(tasks);
    }

    public AsyncResult go(String ruleName, AsyncParam param, long timeout) {
        if (check(ruleName).isPresent()) {
            return trigger.get(ruleName).trigger(param, timeout).load();
        }
        throw new NotTaskRuleException("Gobrs Rule Name Is Error");

    }

    public AsyncResult go(String taskName, AsyncParam param) {
        return go(taskName, param, 0L);
    }


    public synchronized void readyTo(String ruleName) {
        if (trigger == null) {
            loadTrigger(ruleName);
        }
        if (trigger.get(ruleName) == null) {
            loadTriggerForOne(ruleName);
        }
    }

    private void loadTaskFlow(String ruleName) {
        taskFlow = new HashMap<>();
        TaskFlow tf = new TaskFlow();
        tf.setGobrsAsyncProperties(gobrsAsyncProperties);
        taskFlow.put(ruleName, tf);
    }

    private void loadTaskFlowForOne(String ruleName) {
        TaskFlow tf = new TaskFlow();
        tf.setGobrsAsyncProperties(gobrsAsyncProperties);
        taskFlow.put(ruleName, tf);
    }

    private void loadTrigger(String ruleName) {
        trigger = new HashMap<>();
        TaskProcess tr = new TaskProcess(taskFlow.get(ruleName));
        trigger.put(ruleName, tr);
    }

    private void loadTriggerForOne(String taskName) {
        TaskProcess tr = new TaskProcess(taskFlow.get(taskName));
        trigger.put(taskName, tr);
    }

    private Optional<TaskProcess> check(String ruleName) {
        return Optional.ofNullable(trigger.get(ruleName));
    }

}

