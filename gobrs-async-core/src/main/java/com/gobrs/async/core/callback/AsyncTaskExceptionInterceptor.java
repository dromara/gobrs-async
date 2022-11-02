package com.gobrs.async.core.callback;


/**
 * The interface Async com.gobrs.async.com.gobrs.async.test.task com.gobrs.async.exception interceptor.
 *
 * @param <Param> the type parameter
 * @program: gobrs -async
 * @ClassName AsyncExceptionInterceptor
 * @description:
 * @author: sizegang
 * @create: 2022 -02-19 22:41
 * @Version 1.0
 */
public interface AsyncTaskExceptionInterceptor<Param> {
    /**
     * error CallBack
     *
     * @param errorCallback the error com.gobrs.async.callback
     */
    void exception(ErrorCallback<Param> errorCallback);
}
