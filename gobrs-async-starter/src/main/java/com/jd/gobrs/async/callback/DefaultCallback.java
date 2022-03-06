package com.jd.gobrs.async.callback;


import com.jd.gobrs.async.task.TaskResult;

/**
 * 默认回调类，如果不设置的话，会默认给这个回调
 * @author sizegang wrote on 2019-11-19.
 */
public class DefaultCallback<T, V> implements ICallback<T, V> {
    @Override
    public void begin() {
        
    }

    @Override
    public void callback(boolean success, T param, TaskResult<V> workResult) {

    }

}
