package com.gobrs.async.task;

import com.gobrs.async.TaskSupport;

/**
 * @program: gobrs-async-core
 * @ClassName ProxyTask
 * @description:
 * @author: sizegang
 * @create: 2022-04-17
 **/
public class ProxyTask implements GobrsTask{

    private AsyncTask asyncTask;

    @Override
    public void prepare(Object o) {
        asyncTask.prepare(o);
    }

    @Override
    public Object task(Object o, TaskSupport support) {
        return asyncTask.task(o, support);
    }

    @Override
    public boolean nessary(Object o, TaskSupport support) {
        return asyncTask.nessary(o,support);
    }

    @Override
    public void onSuccess(TaskSupport support) {

    }

    @Override
    public void onFail(TaskSupport support) {

    }
}
