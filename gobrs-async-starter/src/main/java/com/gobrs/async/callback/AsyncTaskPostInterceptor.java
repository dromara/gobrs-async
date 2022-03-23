package com.gobrs.async.callback;

/**
 * @program: gobrs-async-core
 * @ClassName PostPostInterceptor
 * @description:
 * @author: sizegang
 * @create: 2022-03-23
 **/
public interface AsyncTaskPostInterceptor {

   void postProcess(Object result,  String taskName);

}
