package com.gobrs.async.callback;

/**
 * @program: gobrs-async-core
 * @ClassName TaskPreInterceptor
 * @description:
 * @author: sizegang
 * @create: 2022-03-23
 **/
public interface AsyncTaskPreInterceptor<P> {

    void preProcess(P params, String taskKey, String taskName);
}
