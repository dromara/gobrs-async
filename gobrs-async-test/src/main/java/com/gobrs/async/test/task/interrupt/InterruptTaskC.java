package com.gobrs.async.test.task.interrupt;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.common.domain.TaskResult;
import com.gobrs.async.core.task.AsyncTask;


/**
 * The type C service.
 *
 * @program: gobrs -async-starter
 * @ClassName CService
 * @description:
 * @author: sizegang
 * @create: 2022 -03-20
 */
@Task
public class InterruptTaskC extends AsyncTask<String, Integer> {

    /**
     * The .
     */
    int i = 10000;


    @Override
    public void prepare(String o) {

    }

    @Override
    public Integer task(String o, TaskSupport support) {
        try {
            System.out.println("InterruptTaskC Begin");
            //获取 所依赖的父任务的结果
            Integer rt = getResult(support);
            String result = getResult(support, InterruptTaskA.class, String.class);
            TaskResult<Integer> tk = getTaskResult(support);
            TaskResult<String> taskResult = getTaskResult(support, InterruptTaskA.class, String.class);
            Thread.sleep(300);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }

            System.out.println("InterruptTaskC Finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean necessary(String o, TaskSupport support) {
        return true;
    }


    @Override
    public void onSuccess(TaskSupport support) {
        // 获取自身task 执行完成之后的结果
        Integer result = getResult(support);

        //获取 任务结果封装 包含执行状态
        TaskResult<Integer> taskResult = getTaskResult(support);
    }

}
