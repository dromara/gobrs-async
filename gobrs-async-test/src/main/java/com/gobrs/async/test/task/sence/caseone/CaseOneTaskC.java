package com.gobrs.async.test.task.sence.caseone;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.AsyncTask;
import org.springframework.stereotype.Component;

/**
 * @program: gobrs-async
 * @ClassName CaseOneTaskC
 * @description:
 * @author: sizegang
 * @create: 2022-10-31
 **/
@AsyncTask
@Component
public class CaseOneTaskC extends com.gobrs.async.core.task.AsyncTask {

    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println("C任务执行");
        return "CResult";
    }
}
