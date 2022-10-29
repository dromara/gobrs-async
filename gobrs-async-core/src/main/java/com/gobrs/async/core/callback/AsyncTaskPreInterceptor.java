package com.gobrs.async.core.callback;

/**
 * The interface Async com.gobrs.async.com.gobrs.async.test.task pre interceptor.
 *
 * @param <P> the type parameter
 * @program: gobrs -async-core
 * @ClassName TaskPreInterceptor
 * @description:
 * @author: sizegang
 * @create: 2022 -03-23
 */
public interface AsyncTaskPreInterceptor<P> {

    /**
     * Pre process.
     *
     * @param params   com.gobrs.async.com.gobrs.async.test.task param
     * @param taskName taskName
     */
    void preProcess(P params, String taskName);
}
