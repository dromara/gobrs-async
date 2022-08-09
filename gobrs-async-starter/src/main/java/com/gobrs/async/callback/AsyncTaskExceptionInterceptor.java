package com.gobrs.async.callback;


/**
 * The interface Async task exception interceptor.
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
     * @param errorCallback the error callback
     */
    void exception(ErrorCallback<Param> errorCallback);
}
