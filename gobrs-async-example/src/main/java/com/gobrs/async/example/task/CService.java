package com.gobrs.async.example.task;

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
public class CService extends AsyncTask<Object, Integer> {

    /**
     * The .
     */
    int i = 10000;


    @Override
    public void prepare(Object o) {

    }

    @Override
    public Integer task(Object o, TaskSupport support) {
        try {
            System.out.println("CService Begin");
            //获取 所依赖的父任务的结果
            AService result = getResult(support, AService.class);

            Thread.sleep(300);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }

            System.out.println("CService Finish");
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
        // 获取自身task 执行完成之后的结果
        Integer result = getResult(support);

        //获取 任务结果封装 包含执行状态
        TaskResult<Integer> taskResult = getTaskResult(support);
    }

    @Override
    public void onFail(TaskSupport support) {
        System.out.println(1/0);
    }
}
