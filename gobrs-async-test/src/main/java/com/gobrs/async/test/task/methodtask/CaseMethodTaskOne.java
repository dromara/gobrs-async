package com.gobrs.async.test.task.methodtask;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Invoke;
import com.gobrs.async.core.anno.MethodComponent;
import com.gobrs.async.core.anno.MethodConfig;
import com.gobrs.async.core.anno.MethodTask;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;

import java.util.HashMap;

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
     * The constant TASK_1_RESULT.
     */
    public static final String TASK_1_RESULT = "task1_result";

    /**
     * Case 1.
     *
     * @return the string
     * @throws InterruptedException the interrupted exception
     */
    @SneakyThrows
    @MethodTask(name = "normal", config = @MethodConfig(failSubExec = true))
    public String normal(TaskSupport taskSupport) {
        HashMap normal = taskSupport.getParam("normal", HashMap.class);
        Thread.sleep(1000);
        Assertions.assertThat(normal).isEqualTo("context");
        return TASK_1_RESULT;
    }

    /**
     * Normal 2 string.
     *
     * @return the string
     */
    @SneakyThrows
    @MethodTask
    public String normal2(String param1, String param2, TaskSupport support) {
        Assertions.assertThat(param1).isEqualTo("context");
        /**
         * 获取 task1 的返回结果
         */
        String task1Result = support.getResult("normal", String.class);
        Assertions.assertThat(task1Result).isEqualTo(TASK_1_RESULT);
        Thread.sleep(1000);
        return "task2";
    }

    /**
     * Throw exception.
     */
    @SneakyThrows
    @MethodTask(invoke = @Invoke(onFail = "demote", rollback = "Exception"), config = @MethodConfig(retryCount = 10))
    public void throwException() {
        System.out.println("will throwException");
        throw new RuntimeException("throwException test");
    }

    /**
     * Demote.
     */
    public void demote() {
        System.out.println("task2 execute fail");
    }

    /**
     * Timeout.
     */
    @SneakyThrows
    @MethodTask
    public void timeout() {
        Thread.sleep(30000);
    }


    /**
     * Rollback 1.
     */
    @SneakyThrows
    @MethodTask(invoke = @Invoke(rollback = "rool1"))
    public void rollback1() {
        System.out.println("rooback1");
        Thread.sleep(1000);
    }


    /**
     * Rollback 2.
     */
    @SneakyThrows
    @MethodTask(invoke = @Invoke(rollback = "rool2"))
    public void rollback2() {
        System.out.println("rollback2");
        Thread.sleep(1000);
    }


    /**
     * Rollback 3.
     */
    @SneakyThrows
    @MethodTask(invoke = @Invoke(rollback = "rool1"), config = @MethodConfig(callback = true))
    public void rollback3() {
        System.out.println("rollback3");
        Thread.sleep(1000);
        System.out.println(1 / 0);

    }

    /**
     * Roo 1.
     */
    public void rool1() {
        System.out.println("rool1 start ....");
    }


    /**
     * Roo 2.
     */
    public void rool2() {
        System.out.println("rool2 start ....");
    }


}
