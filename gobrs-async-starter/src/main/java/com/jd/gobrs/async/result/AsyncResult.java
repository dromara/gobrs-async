package com.jd.gobrs.async.result;

import com.jd.gobrs.async.util.CommonUtils;
import com.jd.gobrs.async.wrapper.TaskWrapper;

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
public class AsyncResult<P> implements Serializable {

    private Long businessId;
    Map<String, TaskWrapper> datasources;

    public P getData(Class clazz) {
        TaskWrapper taskWrapper;
        if (datasources.get(clazz.getSimpleName()) != null) {
            taskWrapper = datasources.get(clazz.getSimpleName());
        } else {
            taskWrapper = datasources.get(CommonUtils.depKey(clazz));
        }
        if (taskWrapper == null) {
            return null;
        }
        return (P) taskWrapper.getWorkResult(businessId);
    }


    public Map<String, TaskWrapper> getData() {
        return datasources;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Map<String, TaskWrapper> getDatasources() {
        return datasources;
    }

    public void setDatasources(Map<String, TaskWrapper> datasources) {
        this.datasources = datasources;
    }


}
