package io.github.memorydoc;

import io.github.memorydoc.callback.ICallback;
import io.github.memorydoc.callback.ITask;
import io.github.memorydoc.gobrs.GobrsTaskFlow;
import io.github.memorydoc.worker.TaskResult;

import java.util.Map;

/**
 * @program: gobrs-async
 * @ClassName TaskBean
 * @description:
 * @author: sizegang
 * @create: 2022-01-26 23:41
 * @Version 1.0
 **/

public class TaskBean implements ITask, ICallback {
    private String name;

    GobrsTaskFlow gobrsTaskFlow;


    @Override
    public void result(boolean success, Object param, TaskResult workResult) {
    }

    @Override
    public Object doTask(Object object, Map allWrappers) {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
