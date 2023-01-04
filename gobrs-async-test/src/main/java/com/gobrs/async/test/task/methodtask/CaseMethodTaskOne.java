package com.gobrs.async.test.task.methodtask;

import com.gobrs.async.core.anno.Invoke;
import com.gobrs.async.core.anno.MethodComponent;
import com.gobrs.async.core.anno.MethodConfig;
import com.gobrs.async.core.anno.MethodTask;
import lombok.SneakyThrows;

/**
 * The type Case method task one.
 *
 * @program: gobrs -async
 * @ClassName CaseMethodTaskOne
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
@MethodComponent
public class CaseMethodTaskOne {

    /**
     * Case 1.
     */
    @MethodTask(name = "task1")
    public void task1() throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("task1");
    }

    /**
     * Case 2.
     *  MethodConfig 中包含当前方法任务所有的可配置项
     *  Invoke 中包含 方法任务中的 方法回调用（成功、失败、前置 ）
     *
     *
     */
    @SneakyThrows
    @MethodTask(name = "task3", invoke = @Invoke(onFail = "task2Fail", rollback = ""), config = @MethodConfig(retryCount = 1))
    public void task2() {
        System.out.println("task2");
        System.out.println(1/0);
        Thread.sleep(1000);
    }


    public void task2Fail(){
        System.out.println("task2 execute fail");
    }


    /**
     * Task 3.
     */
    @SneakyThrows
    @MethodTask(name = "task3")
    public void task3() {
        System.out.println("task3");
        Thread.sleep(2000);

    }

    /**
     * Task 4.
     */
    @SneakyThrows
    @MethodTask(name = "task4")
    public void task4() {
        Thread.sleep(3000);
        System.out.println("task4");
    }
}
