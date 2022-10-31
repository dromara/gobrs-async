package com.gobrs.async.test.task.sence.casethree;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;

/**
 * @program: gobrs-async
 * @ClassName CaseOneTaskD
 * @description:
 * @author: sizegang
 * @create: 2022-10-31
 **/
@Task
public class CaseThreeTaskG extends com.gobrs.async.core.task.AsyncTask {

    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println("G任务执行");
        return "GResult";
    }
}
