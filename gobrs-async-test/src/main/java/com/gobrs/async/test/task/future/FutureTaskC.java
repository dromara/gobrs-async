package com.gobrs.async.test.task.future;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.common.domain.TaskResult;
import com.gobrs.async.core.task.AsyncTask;
import com.gobrs.async.test.task.interrupt.InterruptTaskA;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


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
public class FutureTaskC extends AsyncTask<String, Integer> {

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
            System.out.println("FutureTaskC Begin");

            /**
             * 获取 非父任务的返回结果 此处 可能获取不到
             */
            String result = getResult(support, FutureTaskA.class, String.class);

            Thread.sleep(300);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }
            /**
             * 获取 非父任务的返回结果 应该使用future 方式等待返回结果
             *
             */
            Future<String> aFutureTask = getTaskFuture(support, FutureTaskA.class, String.class);

            try {
                result = aFutureTask.get();
                System.out.println(result);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            /**
             *  也可以直接使用该该方法获取值
             */
            Object taskFutureResult = getTaskFutureResult(support, FutureTaskA.class, String.class, 1000, TimeUnit.MILLISECONDS);


            System.out.println("FutureTaskC Finish");
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
