package com.gobrs.async.autoconfig;


import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.gobrs.async.rule.Rule;

/**
 * The type Gobrs async properties.
 *
 * @author sizegang1
 * @program: gobrs
 * @ClassName BootstrapProperties
 * @description:
 * @author: sizegang
 * @create: 2022 -01-08 17:30
 * @Version 1.0
 * @date 2022 -01-27 22:04
 */
@ConfigurationProperties(prefix = GobrsAsyncProperties.PREFIX)
@Component
public class GobrsAsyncProperties {

    /**
     * The constant PREFIX.
     */
    public static final String PREFIX = "spring.gobrs.async";

    /**
     * Task rules
     */
    private List<Rule> rules;

    /**
     * Task separator
     */
    private String split = ";";

    /**
     * Next task
     */
    private String point = "->";


    /**
     * Whether the execution exception interrupts the workflow
     */
    private boolean taskInterrupt = false;


    /**
     * Whether global parameter dataContext mode  Parameter context
     */
    private boolean paramContext = true;

    /**
     * Whether transaction task
     */
    private boolean transaction = false;

    /**
     * Default timeout
     * @return
     */
    private long timeout = 3000;


    private boolean relyDepend = false;


    /**
     * Gets timeout.
     *
     * @return the timeout
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Sets timeout.
     *
     * @param timeout the timeout
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets split.
     *
     * @return the split
     */
    public String getSplit() {

        return split;
    }

    /**
     * Sets split.
     *
     * @param split the split
     */
    public void setSplit(String split) {
        this.split = split;
    }

    /**
     * Gets point.
     *
     * @return the point
     */
    public String getPoint() {
        return point;
    }

    /**
     * Sets point.
     *
     * @param point the point
     */
    public void setPoint(String point) {
        this.point = point;
    }


    /**
     * Gets rules.
     *
     * @return the rules
     */
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * Sets rules.
     *
     * @param rules the rules
     */
    public void setRules(List<Rule> rules) {
        this.rules = rules;
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
     * Is rely depend boolean.
     *
     * @return the boolean
     */
    public boolean isRelyDepend() {
        return relyDepend;
    }

    /**
     * Sets rely depend.
     *
     * @param relyDepend the rely depend
     */
    public void setRelyDepend(boolean relyDepend) {
        this.relyDepend = relyDepend;
    }


    /**
     * Is param context boolean.
     *
     * @return the boolean
     */
    public boolean isParamContext() {
        return paramContext;
    }

    /**
     * Sets param context.
     *
     * @param paramContext the param context
     */
    public void setParamContext(boolean paramContext) {
        this.paramContext = paramContext;
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