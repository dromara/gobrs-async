package com.gobrs.async.core.callback;

/**
 * The interface Async com.gobrs.async.com.gobrs.async.test.task post interceptor.
 *
 * @program: gobrs -async-core
 * @ClassName PostPostInterceptor
 * @description:
 * @author: sizegang
 * @create: 2022 -03-23
 */
public interface AsyncTaskPostInterceptor<P> {

    /**
     * Post process.
     *
     * @param result   com.gobrs.async.com.gobrs.async.test.task result
     * @param taskName taskName
     */
   default void postProcess(P result,  String taskName){};

}
