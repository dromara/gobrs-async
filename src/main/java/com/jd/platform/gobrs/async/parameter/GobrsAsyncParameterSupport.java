package com.jd.platform.gobrs.async.parameter;

import com.jd.platform.gobrs.async.gobrs.GobrsAsync;
import com.jd.platform.gobrs.async.wrapper.TaskWrapper;

import java.util.Map;

/**
 * @program: gobrs-async
 * @ClassName GobrsParameterSupport
 * @description:
 * @author: sizegang
 * @create: 2022-01-27 02:32
 * @Version 1.0
 **/
public abstract class GobrsAsyncParameterSupport implements GobrsAsync {
    void invokeParams(Map<String, TaskWrapper> taskWrapperMap) {
    }

    public abstract void invoke(Map<String, TaskWrapper> taskWrapperMap);

}
