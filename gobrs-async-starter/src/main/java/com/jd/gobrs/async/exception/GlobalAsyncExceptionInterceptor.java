package com.jd.gobrs.async.exception;

import java.util.concurrent.CompletionException;

/**
 * @program: gobrs-async
 * @ClassName GlobalAsyncExceptionInterceptor
 * @description:
 * @author: sizegang
 * @create: 2022-02-19 22:42
 * @Version 1.0
 **/
public class GlobalAsyncExceptionInterceptor implements AsyncExceptionInterceptor {

    @Override
    public CompletionException exception(Throwable throwable, Boolean state) {
        return new CompletionException(throwable);
    }
}
