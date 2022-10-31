package com.gobrs.async.test.task.sence.caseone;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.AsyncTask;
import org.springframework.stereotype.Component;

/**
 * @program: gobrs-async
 * @ClassName GobrsTaskA
 * @description:
 * @author: sizegang
 * @create: 2022-10-31
 **/
@AsyncTask
@Component
public class CaseOneTaskA extends com.gobrs.async.core.task.AsyncTask {

    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println("A任务执行");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("A任务执行完成");
        return "AResult";
    }
}
