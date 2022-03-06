package com.jd.gobrs.async.example.exception;

import com.jd.gobrs.async.exception.AsyncExceptionInterceptor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletionException;

/**
 * @program: gobrs-async
 * @ClassName GobrsExceptionInter
 * @description: 主流程中断异常自定义处理
 * @author: sizegang
 * @create: 2022-02-19 22:55
 * @Version 1.0
 **/
@Component
public class GobrsExceptionInter implements AsyncExceptionInterceptor {

    @Override
    public CompletionException exception(Throwable throwable, Boolean state) {
        System.out.println("自定义全局异常 exceptor Interceptor 触发");
        return new CompletionException(throwable);
    }
}
