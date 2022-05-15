package com.gobrs.async.report;

/**
 * @program: gobrs-async-core
 * @ClassName Entry
 * @description:
 * @author: sizegang
 * @create: 2022-04-17
 **/
public class Entry {

    private String taskName; // 任务名称

    private String message; // 执行信息


    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
