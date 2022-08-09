package com.gobrs.async.callback;

/**
 * The interface Async task post interceptor.
 *
 * @program: gobrs -async-core
 * @ClassName PostPostInterceptor
 * @description:
 * @author: sizegang
 * @create: 2022 -03-23
 */
public interface AsyncTaskPostInterceptor {

    /**
     * Post process.
     *
     * @param result   task result
     * @param taskName taskName
     */
    void postProcess(Object result,  String taskName);

}
