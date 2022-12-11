package com.gobrs.async.core.config;

import com.gobrs.async.core.common.def.DefaultConfig;
import lombok.Data;

/**
 * The type Gobrs async rule.
 *
 * @program: gobrs -async
 * @ClassName GobrsAsyncRule
 * @description:
 * @author: sizegang
 * @create: 2022 -12-10
 */
public class GobrsAsyncRule {

    private String name;

    private String content;



    /**
     * 执行异常trace log打印
     */
    private Boolean errLogabled = DefaultConfig.ERR_LOGABLED;
    /**
     * 任务执行过程中耗时打印
     */
    private Boolean costLogabled = DefaultConfig.COST_LOGABLED;


    /**
     * Whether the execution com.gobrs.async.exception interrupts the workflow
     * 任务流程 某任务中断是否终止整个任务流程
     */
    private boolean taskInterrupt = false;

    /**
     * 流程事务
     */
    boolean transaction = false;

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {

        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets err logabled.
     *
     * @return the err logabled
     */
    public Boolean getErrLogabled() {
        return errLogabled;
    }

    /**
     * Sets err logabled.
     *
     * @param errLogabled the err logabled
     */
    public void setErrLogabled(Boolean errLogabled) {
        this.errLogabled = errLogabled;
    }

    /**
     * Gets cost logabled.
     *
     * @return the cost logabled
     */
    public Boolean getCostLogabled() {
        return costLogabled;
    }

    /**
     * Sets cost logabled.
     *
     * @param costLogabled the cost logabled
     */
    public void setCostLogabled(Boolean costLogabled) {
        this.costLogabled = costLogabled;
    }

    /**
     * Is task interrupt boolean.
     *
     * @return the boolean
     */
    public boolean isTaskInterrupt() {
        return taskInterrupt;
    }

    /**
     * Sets task interrupt.
     *
     * @param taskInterrupt the task interrupt
     */
    public void setTaskInterrupt(boolean taskInterrupt) {
        this.taskInterrupt = taskInterrupt;
    }

    /**
     * Is transaction boolean.
     *
     * @return the boolean
     */
    public boolean isTransaction() {
        return transaction;
    }

    /**
     * Sets transaction.
     *
     * @param transaction the transaction
     */
    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }
}
