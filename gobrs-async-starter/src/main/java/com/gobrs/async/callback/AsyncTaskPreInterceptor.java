package com.gobrs.async.callback;

/**
 * @program: gobrs-async-core
 * @ClassName TaskPreInterceptor
 * @description:
 * @author: sizegang
 * @create: 2022-03-23
 **/
public interface AsyncTaskPreInterceptor<P> {

    /**
     * @param params task param
     * @param taskName taskName
     */
    void preProcess(P params, String taskName);
}
