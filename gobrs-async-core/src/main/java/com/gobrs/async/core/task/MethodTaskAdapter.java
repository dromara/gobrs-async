package com.gobrs.async.core.task;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.MethodTask;
import com.gobrs.async.core.cache.GCache;
import com.gobrs.async.core.common.domain.MethodTaskMatch;
import com.gobrs.async.core.common.enums.TaskEnum;
import com.gobrs.async.core.common.util.ProxyUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * The type Method task adaptation.
 *
 * @program: gobrs -async
 * @ClassName MethodTaskAdaptation
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
@Slf4j
@Data
public class MethodTaskAdapter extends AsyncTask<Object, Object> {

    private Map<String, MethodTaskMatch> PARAMETERS_CACHE;

    private GCache gCache;

    private Object proxy;

    private Integer type = TaskEnum.METHOD.getType();

    /**
     * The Method task.
     */
    MethodTask methodTask;

    @Override
    public Object task(Object o, TaskSupport support) {
        MethodTaskMatch match = PARAMETERS_CACHE.get("task");
        return ProxyUtil.invokeMethod(match.getMethod(), proxy, match.getParameters());
    }

    @Override
    public boolean necessary(Object o, TaskSupport support) {
        MethodTaskMatch match = PARAMETERS_CACHE.get("necessary");
        return (boolean) ProxyUtil.invokeMethod(match.getMethod(), proxy, match.getParameters());
    }
}
