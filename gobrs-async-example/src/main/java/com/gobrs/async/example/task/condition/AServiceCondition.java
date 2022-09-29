package com.gobrs.async.example.task.condition;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.anno.Task;
import com.gobrs.async.domain.AnyConditionResult;
import com.gobrs.async.task.AsyncTask;
import org.springframework.stereotype.Component;

/**
 * The type A service.
 *
 * @program: gobrs -async-starter
 * @ClassName AService
 * @description:
 * 任务依赖类型
 *  AServiceCondition,BServiceCondition,CServiceCondition->DServiceCondition:anyCondition
 *
 *  简化配置
 *
 *  A,B,C->D:anyCondition
 *
 *  D根据 A,B,C 返回的任务结果中的 AnyCondition 的state状态 进行判断是否继续执行 子任务
 *
 * @author: sizegang
 * @create: 2022 -03-20
 */
@Task(failSubExec = true)
@Component
public class AServiceCondition extends AsyncTask<Object, AnyConditionResult> {

    /**
     * The .
     */
    int i = 10000;

    @Override
    public void prepare(Object o) {


    }

    @Override
    public AnyConditionResult<String> task(Object o, TaskSupport support) {
        AnyConditionResult.Builder<String> builder = AnyConditionResult.builder();
        try {
            System.out.println("AServiceCondition Begin");
            Thread.sleep(300);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }
            System.out.println("AServiceCondition Finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
            builder.setState(false);
        }
        return builder.build();
    }

    @Override
    public boolean nessary(Object o, TaskSupport support) {
        return true;
    }


    @Override
    public void onSuccess(TaskSupport support) {

    }

    @Override
    public void onFail(TaskSupport support) {

    }

}
