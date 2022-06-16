package com.gobrs.async.autoconfig;


import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.gobrs.async.rule.Rule;

/**
 * @author sizegang1
 * @program: gobrs
 * @ClassName BootstrapProperties
 * @description:
 * @author: sizegang
 * @create: 2022-01-08 17:30
 * @Version 1.0
 * @date 2022-01-27 22:04
 **/
@ConfigurationProperties(prefix = GobrsAsyncProperties.PREFIX)
@Component
public class GobrsAsyncProperties {

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


    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getSplit() {

        return split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }


    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public boolean isTaskInterrupt() {
        return taskInterrupt;
    }

    public void setTaskInterrupt(boolean taskInterrupt) {
        this.taskInterrupt = taskInterrupt;
    }

    public boolean isRelyDepend() {
        return relyDepend;
    }

    public void setRelyDepend(boolean relyDepend) {
        this.relyDepend = relyDepend;
    }


    public boolean isParamContext() {
        return paramContext;
    }

    public void setParamContext(boolean paramContext) {
        this.paramContext = paramContext;
    }

    public boolean isTransaction() {
        return transaction;
    }

    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }
}