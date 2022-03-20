package com.gobrs.async.example.inteceptor;

import com.gobrs.async.callback.ErrorCallback;
import com.gobrs.async.exception.AsyncExceptionInterceptor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletionException;

/**
 * @program: gobrs-async-core
 * @ClassName GobrsInterceptor
 * @description:
 * @author: sizegang
 * @create: 2022-03-20
 **/
@Component
public class GobrsInterceptor implements AsyncExceptionInterceptor {

    @Override
    public void exception(ErrorCallback errorCallback) {
        System.out.println("执行了全局异常");
    }
}
