package com.jd.gobrs.async.autoconfig;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: gobrs
 * @ClassName BootstrapProperties
 * @description:
 * @author: sizegang
 * @create: 2022-01-08 17:30
 * @Version 1.0
 * @date 2022-01-27 22:04
 * @author sizegang1
 **/
@ConfigurationProperties(prefix = GobrsAsyncProperties.PREFIX)
@Component
public class GobrsAsyncProperties {

    public static final String PREFIX = "spring.gobrs.async";

    /**
     * 任务规则
     */
    private String rules;

    /**
     * rule task 分隔符
     */
    private String split = ";";

    /**
     * task 指向
     */
    private String point = "->";

    /**
     * 是否必须依赖
     */
    private String must = ":not";

    /**
     * 执行异常是否打断 工作流程
     */
    private boolean taskInterrupt = false;

    /**
     * 默认总任务超时时间 3s
     * @return
     */
    private long timeout = 3000;



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

    public String getMust() {
        return must;
    }

    public void setMust(String must) {
        this.must = must;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public boolean isTaskInterrupt() {
        return taskInterrupt;
    }

    public void setTaskInterrupt(boolean taskInterrupt) {
        this.taskInterrupt = taskInterrupt;
    }
}