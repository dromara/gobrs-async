package com.gobrs.async.example.task;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.task.AsyncTask;
import com.jd.platform.async.callback.ICallback;
import com.jd.platform.async.callback.IWorker;
import com.jd.platform.async.worker.WorkResult;
import com.jd.platform.async.wrapper.WorkerWrapper;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @program: gobrs-async-starter
 * @ClassName AService
 * @description:
 * @author: sizegang
 * @create: 2022-03-20
 **/
@Component
public class AService extends AsyncTask<Object, Object> implements IWorker<String, String>, ICallback<String, String> {

    int i = 10000;

    @Override
    public void prepare(Object o) {


    }

    @Override
    public Object task(Object o, TaskSupport support) {
        try {
            Thread.sleep(300);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }
//            System.out.println("AService 完成");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean nessary(Object o, TaskSupport support) {
        return true;
    }


    @Override
    public void onSuccess(TaskSupport support) {

    }

    @Override
    public void onFail(TaskSupport support) {

    }

    @Override
    public void begin() {

    }

    @Override
    public void result(boolean b, String s, WorkResult<String> workResult) {

    }

    @Override
    public String action(String s, Map<String, WorkerWrapper> map) {
        try {
            Thread.sleep(300);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }
//            System.out.println("AService 完成");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String defaultValue() {
        return null;
    }
}
