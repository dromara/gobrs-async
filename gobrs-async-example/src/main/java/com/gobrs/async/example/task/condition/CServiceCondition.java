package com.gobrs.async.example.task.condition;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.domain.TaskResult;
import com.gobrs.async.task.AsyncTask;
import org.springframework.stereotype.Component;


/**
 * The type C service.
 *
 * @program: gobrs -async-starter
 * @ClassName CService
 * @description:
 * @author: sizegang
 * @create: 2022 -03-20
 */
@Component
public class CServiceCondition extends AsyncTask<String, Boolean> {

    /**
     * The .
     */
    int i = 10000;


    @Override
    public void prepare(String o) {

    }

    @Override
    public Boolean task(String o, TaskSupport support) {
        try {
            System.out.println("CServiceCondition Begin");
            //获取 所依赖的父任务的结果
            Boolean rt = getResult(support);
            String result = getResult(support, AServiceCondition.class, String.class);
            TaskResult<Boolean> tk = getTaskResult(support);
            TaskResult<String> taskResult = getTaskResult(support, AServiceCondition.class, String.class);
            Thread.sleep(5000);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }
            System.out.println("CServiceCondition Finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean nessary(String o, TaskSupport support) {
        return true;
    }


    @Override
    public void onSuccess(TaskSupport support) {
        // 获取自身task 执行完成之后的结果
        Boolean result = getResult(support);

        //获取 任务结果封装 包含执行状态
        TaskResult<Boolean> taskResult = getTaskResult(support);
    }

    @Override
    public void onFail(TaskSupport support) {
    }
}
