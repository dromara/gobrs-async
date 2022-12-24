package com.gobrs.async.core.common.domain;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Async result.
 *
 * @program: gobrs -async-starter
 * @ClassName AsyncResult 流程执行结果封装
 * @description: Process results
 * @author: sizegang
 * @create: 2022 -03-19
 */
public class AsyncResult implements Serializable {

    /**
     * 整流程 执行结果code
     */
    private Integer executeCode;

    private Integer cusCode;

    /**
     * 整流程 执行是否成功
     */
    private boolean status;

    /**
     * 执行结果封装
     * key com.gobrs.async.test.task 类
     * value 执行结果 （单任务）
     */
    private Map<Class, TaskResult> resultMap = new HashMap();


    /**
     * Gets execute code.
     *
     * @return the execute code
     */
    public Integer getExecuteCode() {
        return executeCode;
    }

    /**
     * Sets execute code.
     *
     * @param executeCode the execute code
     */
    public void setExecuteCode(Integer executeCode) {
        this.executeCode = executeCode;
    }


    /**
     * Gets result map.
     *
     * @return the result map
     */
    public Map<Class, TaskResult> getResultMap() {
        return resultMap;
    }

    /**
     * Sets result map.
     *
     * @param resultMap the result map
     */
    public void setResultMap(Map<Class, TaskResult> resultMap) {
        this.resultMap = resultMap;
    }

    /**
     * Gets cus code.
     *
     * @return the cus code
     */
    public Integer getCusCode() {
        return cusCode;
    }

    /**
     * Sets cus code.
     *
     * @param cusCode the cus code
     */
    public void setCusCode(Integer cusCode) {
        this.cusCode = cusCode;
    }


    /**
     * Is status boolean.
     *
     * @return the boolean
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(boolean status) {
        this.status = status;
    }
}
