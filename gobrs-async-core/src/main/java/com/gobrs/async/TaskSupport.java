package com.gobrs.async;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: gobrs-async-starter
 * @ClassName TaskSupport
 * @description:
 * @author: sizegang
 * @create: 2022-03-20
 **/

public class TaskSupport {


    private Object param;

    private Map<Class, Object> resultMap = new HashMap();


    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    public Map<Class, Object> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<Class, Object> resultMap) {
        this.resultMap = resultMap;
    }
}
