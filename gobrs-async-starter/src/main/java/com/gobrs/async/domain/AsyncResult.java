package com.gobrs.async.domain;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: gobrs-async-starter
 * @ClassName AsyncResult
 * @description: Process results
 * @author: sizegang
 * @create: 2022-03-19
 **/
public class AsyncResult implements Serializable {

    private Integer expCode;
    private boolean success;
    private Map<Class, TaskResult> resultMap = new HashMap();

    public Integer getExpCode() {
        return expCode;
    }

    public void setExpCode(Integer expCode) {
        this.expCode = expCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Map<Class, TaskResult> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<Class, TaskResult> resultMap) {
        this.resultMap = resultMap;
    }
}
