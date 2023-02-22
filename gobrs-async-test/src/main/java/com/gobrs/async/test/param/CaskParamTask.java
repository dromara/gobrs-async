package com.gobrs.async.test.param;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;
import org.json.JSONObject;

/**
 * @program: gobrs-async
 * @ClassName GobrsTaskA
 * @description:
 * @author: sizegang
 * @create: 2022-10-31
 **/
@Task
public class CaskParamTask extends AsyncTask<Object, Object> {

    @Override
    public Object task(Object param, TaskSupport support) {
        /**
         * debug 查看参数类型
         */
        System.out.println(param);
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
