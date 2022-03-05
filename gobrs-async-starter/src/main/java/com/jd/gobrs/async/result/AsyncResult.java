package com.jd.gobrs.async.result;

import com.jd.gobrs.async.gobrs.GobrsAsyncSupport;

import java.io.Serializable;
import java.util.Map;

/**
 * @program: gobrs-async
 * @ClassName AsyncResult
 * @description:
 * @author: sizegang
 * @create: 2022-02-26 14:47
 * @Version 1.0
 **/
public class AsyncResult implements Serializable {

    private Map resultMap;
    GobrsAsyncSupport support;
    private Integer expCode;

    public Map getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map resultMap) {
        this.resultMap = resultMap;
    }

    public GobrsAsyncSupport getSupport() {
        return support;
    }

    public void setSupport(GobrsAsyncSupport support) {
        this.support = support;
    }

    public Integer getExpCode() {
        return expCode;
    }

    public void setExpCode(Integer expCode) {
        this.expCode = expCode;
    }
}
