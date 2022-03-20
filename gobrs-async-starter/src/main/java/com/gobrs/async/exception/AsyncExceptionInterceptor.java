package com.gobrs.async.exception;

import java.util.concurrent.CompletionException;

/**
 * @program: gobrs-async
 * @ClassName AsyncExceptionInterceptor
 * @description:
 * @author: sizegang
 * @create: 2022-02-19 22:41
 * @Version 1.0
 **/
public interface AsyncExceptionInterceptor {
    CompletionException exception(Throwable throwable, Boolean state);
}
