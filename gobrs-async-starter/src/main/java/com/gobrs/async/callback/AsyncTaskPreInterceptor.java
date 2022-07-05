package com.gobrs.async.callback;

/**
 * The interface Async task pre interceptor.
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
     * @param params   task param
     * @param taskName taskName
     */
    void preProcess(P params, String taskName);
}
