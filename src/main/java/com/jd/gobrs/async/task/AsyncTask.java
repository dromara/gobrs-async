package com.jd.gobrs.async.task;

import com.jd.gobrs.async.callback.ICallback;
import com.jd.gobrs.async.callback.ITask;

/**
 * @program: gobrs-async-starter
 * @ClassName AsyncTask
 * @description:
 * @author: sizegang
 * @create: 2022-01-28 00:16
 * @Version 1.0
 **/
public interface AsyncTask<T, V> extends ITask<T, V>, ICallback<T, V> {
    /**
     * 根据业务实现 判断是否需要执行当前task
     *
     * @param t
     * @return
     */
    @Override
    boolean nessary(T t);
}
