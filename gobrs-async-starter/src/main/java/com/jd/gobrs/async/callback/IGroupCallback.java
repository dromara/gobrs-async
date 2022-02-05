package com.jd.gobrs.async.callback;

import com.jd.gobrs.async.wrapper.TaskWrapper;

import java.util.List;

/**
 * 如果是异步执行整组的话，可以用这个组回调。不推荐使用
 * @author sizegang wrote on 2019-11-19.
 */
public interface IGroupCallback {
    /**
     * 成功后，可以从wrapper里去getWorkResult
     */
    void success(List<TaskWrapper> taskWrappers);
    /**
     * 失败了，也可以从wrapper里去getWorkResult
     */
    void failure(List<TaskWrapper> taskWrappers, Exception e);
}
