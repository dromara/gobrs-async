package com.gobrs.async.core.log;

/**
 * The type Log tracer.
 *
 * @program: gobrs -async
 * @ClassName LogTracer
 * @description:
 * @author: sizegang
 * @create: 2022 -10-28
 */
public class LogTracer {

    /**
     * 任务执行时间
     */
    private Long cost;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务昵称
     */
    private String nickName;

    /**
     * 任务异常错误信息
     */
    private String errorMessage;

    /**
     * 任务执行状态
     */
    private Boolean executeState;


    /**
     * Gets cost.
     *
     * @return the cost
     */
    public Long getCost() {
        return cost;
    }

    /**
     * Sets cost.
     *
     * @param cost the cost
     */
    public void setCost(Long cost) {
        this.cost = cost;
    }

    /**
     * Gets com.gobrs.async.com.gobrs.async.test.task name.
     *
     * @return the com.gobrs.async.com.gobrs.async.test.task name
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Sets com.gobrs.async.com.gobrs.async.test.task name.
     *
     * @param taskName the com.gobrs.async.com.gobrs.async.test.task name
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Gets nick name.
     *
     * @return the nick name
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * Sets nick name.
     *
     * @param nickName the nick name
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * Gets error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets error message.
     *
     * @param errorMessage the error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets execute state.
     *
     * @return the execute state
     */
    public Boolean getExecuteState() {
        return executeState;
    }

    /**
     * Sets execute state.
     *
     * @param executeState the execute state
     */
    public void setExecuteState(Boolean executeState) {
        this.executeState = executeState;
    }

    /**
     * Instantiates a new Log tracer.
     *
     * @param cost         the cost
     * @param taskName     the com.gobrs.async.com.gobrs.async.test.task name
     * @param nickName     the nick name
     * @param errorMessage the error message
     * @param executeState the execute state
     */
    public LogTracer(Long cost, String taskName, String nickName, String errorMessage, Boolean executeState) {
        this.cost = cost;
        this.taskName = taskName;
        this.nickName = nickName;
        this.errorMessage = errorMessage;
        this.executeState = executeState;
    }

    @Override
    public String toString() {
        return "LogTracer{" +
                "cost=" + cost +
                ", taskName='" + taskName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", executeState=" + executeState +
                '}';
    }
}
