package com.gobrs.async.example.config;

import com.alibaba.fastjson.JSONObject;
import com.gobrs.async.callback.AsyncTaskExceptionInterceptor;
import com.gobrs.async.callback.ErrorCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @program: m-detail
 * @ClassName TaskExceptionInterceptor
 * @description:
 * @author: sizegang
 * @create: 2022-03-30
 **/
@Slf4j
@Component
public class TaskExceptionInterceptor implements AsyncTaskExceptionInterceptor<Map> {

    @Override
    public void exception(ErrorCallback<Map> errorCallback) {
        String msg = String.format("【Gobrs-Async全局任务监控】%s任务, 执行失败, 请求参数%s, 异常%s, 如需查看详情, 请前往泰山logbook查询",
                JSONObject.toJSONString(errorCallback.getParam().get()), errorCallback.getTask().getName(), errorCallback.getThrowable());
        log.error(msg);
    }

}
