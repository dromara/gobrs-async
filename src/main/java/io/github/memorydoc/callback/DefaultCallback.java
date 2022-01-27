package io.github.memorydoc.callback;


import io.github.memorydoc.worker.TaskResult;

/**
 * 默认回调类，如果不设置的话，会默认给这个回调
 * @author sizegang wrote on 2019-11-19.
 */
public class DefaultCallback<T, V> implements ICallback<T, V> {
    @Override
    public void begin() {
        
    }

    @Override
    public void result(boolean success, T param, TaskResult<V> workResult) {

    }

}
