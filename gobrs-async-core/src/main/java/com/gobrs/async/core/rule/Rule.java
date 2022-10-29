package com.gobrs.async.core.rule;

import java.io.Serializable;

/**
 * The type Rule.
 *
 * @author sizegang1
 * @program: gobrs -async
 * @ClassName Rule
 * @description:
 * @author: sizegang
 * @create: 2022 -01-26 01:39
 * @Version 1.0
 * @date 2022 -01-27
 */
public class Rule implements Serializable {

    private String name;

    private String content;

    private Boolean logSwitch;


    /**
     * Whether the execution com.gobrs.async.exception interrupts the workflow
     */
    private boolean taskInterrupt = false;


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
     * Gets com.gobrs.async.log switch.
     *
     * @return the com.gobrs.async.log switch
     */
    public Boolean getLogSwitch() {
        return logSwitch;
    }

    /**
     * Sets com.gobrs.async.log switch.
     *
     * @param logSwitch the com.gobrs.async.log switch
     */
    public void setLogSwitch(Boolean logSwitch) {
        this.logSwitch = logSwitch;
    }

    /**
     * Is com.gobrs.async.com.gobrs.async.test.task interrupt boolean.
     *
     * @return the boolean
     */
    public boolean isTaskInterrupt() {
        return taskInterrupt;
    }

    /**
     * Sets com.gobrs.async.com.gobrs.async.test.task interrupt.
     *
     * @param taskInterrupt the com.gobrs.async.com.gobrs.async.test.task interrupt
     */
    public void setTaskInterrupt(boolean taskInterrupt) {
        this.taskInterrupt = taskInterrupt;
    }
}
