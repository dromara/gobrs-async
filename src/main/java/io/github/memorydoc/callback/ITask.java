package io.github.memorydoc.callback;

import java.util.Map;

import io.github.memorydoc.gobrs.GobrsBean;
import io.github.memorydoc.wrapper.TaskWrapper;

/**
 * 每个最小执行单元需要实现该接口
 *
 * @author sizegang wrote on 2019-11-19.
 */
@FunctionalInterface
public interface ITask<T, V> extends GobrsBean {
    /**
     * 在这里做耗时操作，如rpc请求、IO等
     *
     * @param object      object
     * @param allWrappers 任务包装
     */
    V doTask(T object, Map<String, TaskWrapper> allWrappers);

    /**
     * 超时、异常时，返回的默认值
     *
     * @return 默认值
     */
    default V defaultValue() {
        return null;
    }

    /**
     * 根据业务实现 判断是否需要执行当前task
     * @param t
     * @return
     */
    default boolean nessary(T t) {
        return true;
    }


}
