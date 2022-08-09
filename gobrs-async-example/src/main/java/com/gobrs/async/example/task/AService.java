package com.gobrs.async.example.task;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.anno.Task;
import com.gobrs.async.task.AsyncTask;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;

/**
 * The type A service.
 *
 * @program: gobrs -async-starter
 * @ClassName AService
 * @description:
 * @author: sizegang
 * @create: 2022 -03-20
 */
@Task(failSubExec = true)
@Component
public class AService extends AsyncTask<String, String> {

    /**
     * The .
     */
    int i = 10000;

    @Override
    public void prepare(String o) {


    }

    @Override
    public String task(String o, TaskSupport support) {
        System.out.println(o);

        try {
            System.out.println("AService Begin");
            Thread.sleep(300);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }
            System.out.println("AService Finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "result";
    }

    @Override
    public boolean nessary(String o, TaskSupport support) {
        return true;
    }


    @Override
    public void onSuccess(TaskSupport support) {

    }

    @Override
    public void onFail(TaskSupport support) {

    }
}
