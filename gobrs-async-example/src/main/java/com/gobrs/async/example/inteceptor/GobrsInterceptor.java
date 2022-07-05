package com.gobrs.async.example.inteceptor;

import com.gobrs.async.callback.AsyncTaskExceptionInterceptor;
import com.gobrs.async.callback.ErrorCallback;
import com.gobrs.async.util.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * The type Gobrs interceptor.
 *
 * @program: gobrs -async-core
 * @ClassName GobrsInterceptor
 * @description: 异常拦截器
 * @author: sizegang
 * @create: 2022 -03-20
 */
@Slf4j
@Component
public class GobrsInterceptor implements AsyncTaskExceptionInterceptor {


    @SneakyThrows
    @Override
    public void exception(ErrorCallback errorCallback) {

        log.error("Execute global interceptor  error{}", JsonUtil.obj2String(errorCallback.getThrowable()));
    }
}
