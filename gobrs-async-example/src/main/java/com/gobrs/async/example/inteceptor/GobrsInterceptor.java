package com.gobrs.async.example.inteceptor;

import com.alibaba.fastjson.JSONObject;
import com.gobrs.async.callback.AsyncTaskExceptionInterceptor;
import com.gobrs.async.callback.ErrorCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @program: gobrs-async-core
 * @ClassName GobrsInterceptor
 * @description: 异常拦截器
 * @author: sizegang
 * @create: 2022-03-20
 **/
@Slf4j
@Component
public class GobrsInterceptor implements AsyncTaskExceptionInterceptor {

    @Override
    public void exception(ErrorCallback errorCallback) {
        log.error("Execute global interceptor  error{}", JSONObject.toJSONString(errorCallback.getThrowable()));
    }
}
