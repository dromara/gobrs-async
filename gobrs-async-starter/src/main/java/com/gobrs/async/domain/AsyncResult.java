package com.gobrs.async.domain;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Async result.
 *
 * @program: gobrs -async-starter
 * @ClassName AsyncResult
 * @description: Process results
 * @author: sizegang
 * @create: 2022 -03-19
 */
public class AsyncResult implements Serializable {

    private Integer expCode;
    private boolean success;
    private Map<Class, TaskResult> resultMap = new HashMap();

    /**
     * Gets exp code.
     *
     * @return the exp code
     */
    public Integer getExpCode() {
        return expCode;
    }

    /**
     * Sets exp code.
     *
     * @param expCode the exp code
     */
    public void setExpCode(Integer expCode) {
        this.expCode = expCode;
    }

    /**
     * Is success boolean.
     *
     * @return the boolean
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets success.
     *
     * @param success the success
     */
    public void setSuccess(boolean success) {
        this.success = success;
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
}
