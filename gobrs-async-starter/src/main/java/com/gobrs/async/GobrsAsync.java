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

    private Map<String, TaskTrigger> trigger;


    public TaskReceive begin(String taskName, AsyncTask... tasks) {
        return taskFlow.get(taskName).start(tasks);
    }


    public TaskReceive begin(String ruleName, List<AsyncTask> asyncTasks, boolean reload) {
        if (taskFlow == null) {
            loadTaskFlow(ruleName);
        }
        if (taskFlow.get(ruleName) == null) {
            loadTaskFlowForOne(ruleName);
        }
        if(reload){
            loadTaskFlowForOne(ruleName);
        }
        return taskFlow.get(ruleName).start(asyncTasks);
    }


    public TaskReceive begin(String ruleName, List<AsyncTask> asyncTasks) {
        return begin(ruleName, asyncTasks, false);
    }

    public TaskReceive after(String taskName, AsyncTask... tasks) {
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
        readyTo(ruleName, false);
    }

    public synchronized void readyTo(String ruleName, boolean reload) {
        if (trigger == null) {
            loadTrigger(ruleName);
        }
        if (trigger.get(ruleName) == null) {
            loadTriggerForOne(ruleName);
        }
        if(reload){
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
        TaskTrigger tr = new TaskTrigger(taskFlow.get(ruleName));
        trigger.put(ruleName, tr);
    }

    private void loadTriggerForOne(String taskName) {
        TaskTrigger tr = new TaskTrigger(taskFlow.get(taskName));
        trigger.put(taskName, tr);
    }

    private Optional<TaskTrigger> check(String ruleName) {
        return Optional.ofNullable(trigger.get(ruleName));
    }

}

