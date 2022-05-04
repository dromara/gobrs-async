package com.gobrs.async.example.task;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.task.AsyncTask;
import org.springframework.stereotype.Component;


/**
 * @program: gobrs-async-starter
 * @ClassName CService
 * @description:
 * @author: sizegang
 * @create: 2022-03-20
 **/
@Component
public class CService extends AsyncTask<Object, Object> {

    int i = 10000;


    @Override
    public void prepare(Object o) {


    }

    @Override
    public Object task(Object o, TaskSupport support) {
        try {
            System.out.println("CService开始执行");
            String result = getResult(support, AService.class, String.class);
            System.out.println(result);

            Thread.sleep(3000);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }
            System.out.println("CService 完成");
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
        System.out.println(1/0);
    }
}
