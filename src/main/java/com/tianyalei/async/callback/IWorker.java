package com.tianyalei.async.callback;

/**
 * 每个最小执行单元需要实现该接口
 * @author wuweifeng wrote on 2019-11-19.
 */
public interface IWorker<T, V> {
    /**
     * 在这里做耗时操作，如rpc请求、IO等
     *
     * @param object
     *         object
     */
    V action(T object);

    /**
     * 超时、异常时，返回的默认值
     * @return 默认值
     */
    V defaultValue();
}
