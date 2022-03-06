package com.jd.gobrs.async.callback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.gobrs.async.gobrs.GobrsAsyncSupport;
import com.jd.gobrs.async.task.TaskResult;
import com.jd.gobrs.async.wrapper.TaskWrapper;

/**
 * 每个最小执行单元需要实现该接口
 *
 * @author sizegang wrote on 2019-11-19.
 */
@FunctionalInterface
public interface ITask<T, V> {
    /**
     * 在这里做耗时操作，如rpc请求、IO等
     * @param params  params
     * @param support support 任务流对象
     */
    V task(T params, GobrsAsyncSupport support);

    /**
     * 超时、异常时，返回的默认值
     *
     * @return 默认值
     */
    default V defaultValue() {
        return null;
    }

    /**
     *
     * @param params  任务参数
     * @param support 任务流对象
     * @return
     */
    default boolean nessary(T params, GobrsAsyncSupport support) {
        return true;
    }


}
