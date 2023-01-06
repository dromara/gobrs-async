package com.gobrs.async.test.task.methodtask;

import com.gobrs.async.core.anno.Invoke;
import com.gobrs.async.core.anno.MethodComponent;
import com.gobrs.async.core.anno.MethodConfig;
import com.gobrs.async.core.anno.MethodTask;
import com.gobrs.async.core.task.MTaskContext;
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
    public String task1(String text, MTaskContext<String> context) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("task1");
        String result = "task1";
        return result;
    }

    /**
     * Case 2.
     * MethodConfig 中包含当前方法任务所有的可配置项
     * Invoke 中包含 方法任务中的 方法回调用（成功、失败、前置 ）
     */
    @SneakyThrows
    @MethodTask(invoke = @Invoke(onFail = "task2Fail", rollback = ""), config = @MethodConfig(retryCount = 1))
    public String task2(String text, MTaskContext<String> context) {
        String param = context.getParam();

        System.out.println("task2 的参数是 " + param);

        /**
         * 获取 task1 的返回结果
         */
        String task1Result = context.getTaskResult("task1", String.class);
        System.out.println("task1 的结果是 " + task1Result);
        System.out.println("task2");
        Thread.sleep(1000);
        return "task2";
    }


    public void task2Fail() {
        System.out.println("task2 execute fail");
    }


    /**
     * Task 3.
     */
    @SneakyThrows
    @MethodTask(name = "task3")
    public void task3(MTaskContext<String> context) {
        System.out.println("task3");
        Thread.sleep(2000);

    }

    /**
     * Task 4.
     */
    @SneakyThrows
    @MethodTask(name = "task4")
    public void task4(String s, String y, MTaskContext context) {
        Thread.sleep(3000);
        System.out.println("task4");
    }
}
