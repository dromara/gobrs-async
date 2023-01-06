package com.gobrs.async.core.task;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.MethodTask;
import com.gobrs.async.core.common.def.DefaultConfig;
import com.gobrs.async.core.common.domain.AsyncParam;
import com.gobrs.async.core.common.domain.MethodTaskMatch;
import com.gobrs.async.core.common.exception.AsyncTaskNotFoundException;
import com.gobrs.async.core.common.exception.MethodTaskArgumentException;
import com.gobrs.async.core.common.util.ProxyUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;

import java.lang.reflect.Parameter;
import java.util.*;

import static com.gobrs.async.core.common.domain.GobrsTaskMethodEnum.NECESSARY;
import static com.gobrs.async.core.common.domain.GobrsTaskMethodEnum.ONFAIL;
import static com.gobrs.async.core.common.domain.GobrsTaskMethodEnum.ONSUCCESS;
import static com.gobrs.async.core.common.domain.GobrsTaskMethodEnum.PREPARE;
import static com.gobrs.async.core.common.domain.GobrsTaskMethodEnum.ROLLBACK;
import static com.gobrs.async.core.common.domain.GobrsTaskMethodEnum.TASK;

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

    @Override
    public Object task(Object parameter, TaskSupport support) {
        MethodTaskMatch match = PARAMETERS_CACHE.get(TASK.getMethod());
        Object[] params = createParams(parameter, support, match);
        return ProxyUtil.invokeMethod(match.getMethod(), proxy, params);
    }


    @Override
    public boolean necessary(Object parameter, TaskSupport support) {
        MethodTaskMatch match = PARAMETERS_CACHE.get(NECESSARY.getMethod());
        if (Objects.isNull(match)) {
            return DefaultConfig.TASK_NECESSARY;
        }
        Optional<Object> o = Optional.ofNullable(doProxy(match, parseParam(parameter)));
        if (o.isPresent()) {
            return (Boolean) o.get();
        }
        return true;

    }

    @Override
    public void onFail(TaskSupport support, Exception exception) {
        MethodTaskMatch match = PARAMETERS_CACHE.get(ONFAIL.getMethod());
        if (Objects.isNull(match)) {
            super.onFail(support, exception);
            return;
        }

        doProxy(match, createParams(support.getParam(), support, match));
    }



    @Override
    public void onSuccess(TaskSupport support) {
        MethodTaskMatch match = PARAMETERS_CACHE.get(ONSUCCESS.getMethod());
        if (Objects.isNull(match)) {
            super.onSuccess(support);
            return;
        }
        doProxy(match, createParams(support.getParam(), support, match));
    }




    @Override
    public void prepare(Object parameter) {
        MethodTaskMatch match = PARAMETERS_CACHE.get(PREPARE.getMethod());
        if (Objects.isNull(match)) {
            super.prepare(parameter);
            return;
        }
        doProxy(match, parseParam(parameter));
    }

    @Override
    public void rollback(Object parameter) {
        MethodTaskMatch match = PARAMETERS_CACHE.get(ROLLBACK.getMethod());
        if (Objects.isNull(match)) {
            super.rollback(parameter);
            return;
        }
        doProxy(match, parseParam(parameter));
    }


    /**
     * 构建参数
     * @param parameter
     * @param support
     * @param match
     * @return
     */
    private Object[] createParams(Object parameter, TaskSupport support, MethodTaskMatch match) {
        if (Objects.isNull(match)) {
            throw new AsyncTaskNotFoundException(String.format(" MethodTask not found %s", getName()));
        }

        final Parameter[] parameters = match.getMethod().getParameters();
        parameter = parameterTransfer(parameter);
        List<Object> req =
                Optional.ofNullable(parameter)
                        .map(p -> (List<Object>) p)
                        .orElse(Lists.newArrayList());

        if (parameters.length != req.size()) {
            log.error(String.format(" Parameter mismatch %s", getName()));
            // throw new MethodTaskArgumentException(String.format(" Parameter mismatch %s", getName()));
        }

        Object[] params = new Object[parameters.length];
        if (params.length == 0) {
            return null;
        }
        for (int i = 0; i < parameters.length; i++) {
            if (TaskSupport.class.isAssignableFrom(parameters[i].getType())) {
                params[i] = support;
                continue;
            }
            try {
                params[i] = req.get(i);
            } catch (Exception exception) {
                params[i] = null;
            }
        }
        return params;
    }


    /**
     * invoke
     * @param match
     * @param parameter
     * @return
     */
    private Object doProxy(MethodTaskMatch match, Object[] parameter) {
        return ProxyUtil.invokeMethod(match.getMethod(), proxy, parameter);
    }

    /**
     * 参数解析
     * @param parameter
     * @return
     */
    private Object[] parseParam(Object parameter) {
        Object params = parameterTransfer(parameter);
        Object[] paramsArray = params != null ? new Object[]{params} : null;
        return paramsArray;
    }

    /**
     * 参数转换
     * @param parameter
     * @return
     */
    private Object parameterTransfer(Object parameter) {
        if (parameter instanceof AsyncParam) {
            Object resp = ((AsyncParam<Object>) parameter).get();
            parameter = resp instanceof HashMap ? ((HashMap<?, ?>) resp).get(getName()) : resp;
        }
        return parameter;
    }
}
