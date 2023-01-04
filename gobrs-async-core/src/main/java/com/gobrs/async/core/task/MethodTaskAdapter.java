package com.gobrs.async.core.task;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.MethodTask;
import com.gobrs.async.core.common.def.DefaultConfig;
import com.gobrs.async.core.common.domain.MethodTaskMatch;
import com.gobrs.async.core.common.exception.AsyncTaskNotFoundException;
import com.gobrs.async.core.common.util.ProxyUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

import static com.gobrs.async.core.common.domain.GobrsTaskMethodEnum.*;

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

    private Object proxy;

    /**
     * The Method task.
     */
    MethodTask methodTask;

    @Override
    public Object task(Object o, TaskSupport support) {
        MethodTaskMatch match = PARAMETERS_CACHE.get(TASK.getMethod());
        if (Objects.isNull(match)) {
            throw new AsyncTaskNotFoundException(String.format(" MethodTask not found %s", getName()));
        }
        return ProxyUtil.invokeMethod(match.getMethod(), proxy, match.getParameters());
    }

    @Override
    public boolean necessary(Object o, TaskSupport support) {
        MethodTaskMatch match = PARAMETERS_CACHE.get(NECESSARY.getMethod());
        if (Objects.isNull(match)) {
            return DefaultConfig.TASK_NECESSARY;
        }
        return (Boolean) doProxy(match);
    }

    @Override
    public void onFail(TaskSupport support, Exception exception) {
        MethodTaskMatch match = PARAMETERS_CACHE.get(ONFAIL.getMethod());
        if (Objects.isNull(match)) {
            super.onFail(support, exception);
            return;
        }
        doProxy(match);
    }

    private Object doProxy(MethodTaskMatch match) {
        return ProxyUtil.invokeMethod(match.getMethod(), proxy, match.getParameters());
    }

    @Override
    public void onSuccess(TaskSupport support) {
        MethodTaskMatch match = PARAMETERS_CACHE.get(ONSUCCESS.getMethod());
        if (Objects.isNull(match)) {
            super.onSuccess(support);
            return;
        }
        doProxy(match);
    }

    @Override
    public void prepare(Object o) {
        MethodTaskMatch match = PARAMETERS_CACHE.get(PREPARE.getMethod());
        if (Objects.isNull(match)) {
            super.prepare(o);
            return;
        }
        doProxy(match);
    }

    @Override
    public void rollback(Object o) {
        MethodTaskMatch match = PARAMETERS_CACHE.get(ROLLBACK.getMethod());
        if (Objects.isNull(match)) {
            super.rollback(o);
            return;
        }
        doProxy(match);
    }
}
