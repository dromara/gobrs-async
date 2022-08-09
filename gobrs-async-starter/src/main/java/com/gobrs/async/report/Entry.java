package com.gobrs.async.report;

/**
 * The type Entry.
 *
 * @program: gobrs -async-core
 * @ClassName Entry
 * @description:
 * @author: sizegang
 * @create: 2022 -04-17
 */
public class Entry {

    private String taskName; // 任务名称

    private String message; // 执行信息


    /**
     * Gets task name.
     *
     * @return the task name
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Sets task name.
     *
     * @param taskName the task name
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
