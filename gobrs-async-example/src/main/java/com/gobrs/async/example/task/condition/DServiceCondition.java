package com.gobrs.async.example.task.condition;

import com.gobrs.async.TaskSupport;
import com.gobrs.async.task.AsyncTask;
import org.springframework.stereotype.Component;

/**
 * The type D service.
 *
 * @program: gobrs -async-starter
 * @ClassName DService
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
 *
 *
 * @author: sizegang
 * @create: 2022 -03-20
 */
@Component
public class DServiceCondition extends AsyncTask<Object, Boolean> {

    /**
     * The .
     */
    int i = 1;

    @Override
    public void prepare(Object o) {

    }

    @Override
    public Boolean task(Object o, TaskSupport support) {
//        System.out.println("DServiceCondition Begin");
        for (int i1 = 0; i1 < i; i1++) {
            i1 += i1;
        }
//        System.out.println("DServiceCondition Finish");
        return true;
    }

    @Override
    public boolean nessary(Object o, TaskSupport support) {
        return true;
    }


    @Override
    public void onSuccess(TaskSupport support) {

    }

}
