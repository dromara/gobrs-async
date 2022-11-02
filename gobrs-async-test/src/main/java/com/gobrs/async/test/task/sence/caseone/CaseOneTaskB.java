package com.gobrs.async.test.task.sence.caseone;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;

/**
 * @program: gobrs-async
 * @ClassName GobrsTaskB
 * @description:
 * @author: sizegang
 * @create: 2022-10-31
 **/
@Task
public class CaseOneTaskB extends AsyncTask {

    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println("B任务执行");
        return "BResult";
    }
}
