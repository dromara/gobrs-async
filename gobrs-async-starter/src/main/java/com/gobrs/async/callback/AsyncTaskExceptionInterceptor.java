package com.gobrs.async.callback;

import com.gobrs.async.callback.ErrorCallback;

import java.util.concurrent.CompletionException;

/**
 * @program: gobrs-async
 * @ClassName AsyncExceptionInterceptor
 * @description:
 * @author: sizegang
 * @create: 2022-02-19 22:41
 * @Version 1.0
 **/
public interface AsyncTaskExceptionInterceptor<Param> {
    /**
     * error CallBack
     * @param errorCallback
     */
     void exception(ErrorCallback<Param> errorCallback);
}
