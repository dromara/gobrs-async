package com.gobrs.async.core.common.domain;

/**
 * The enum Gobrs task method enum.
 *
 * @program: gobrs -async
 * @ClassName GobrsTaskMethodEnum
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
public enum GobrsTaskMethodEnum {
    /**
     * Task gobrs task method enum.
     */
    TASK("task", "任务执行"),
    /**
     * Onfail gobrs task method enum.
     */
    ONFAIL("onFail", "失败回调"),
    /**
     * Onsuccess gobrs task method enum.
     */
    ONSUCCESS("onSuccess", "成功回调"),
    /**
     * Necessary gobrs task method enum.
     */
    NECESSARY("necessary", "执行任务必要条件"),
    /**
     * Rollback gobrs task method enum.
     */
    ROLLBACK("rollback", "事务回滚"),

    PREPARE("prepare", "前置调用");

    private String method;


    private String desc;


    GobrsTaskMethodEnum(String method, String desc) {
        this.method = method;
        this.desc = desc;
    }

    /**
     * Gets method.
     *
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets method.
     *
     * @param method the method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets desc.
     *
     * @param desc the desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
