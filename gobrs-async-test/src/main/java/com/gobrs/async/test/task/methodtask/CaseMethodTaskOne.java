package com.gobrs.async.test.task.methodtask;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Invoke;
import com.gobrs.async.core.anno.MethodComponent;
import com.gobrs.async.core.anno.MethodConfig;
import com.gobrs.async.core.anno.MethodTask;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;

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

    public static final String TASK_1_RESULT = "task1_result";
    public static final String CONTEXT = "context";

    /**
     * Case 1.
     */
    @MethodTask(name = "normal")
    public String normal(String context) throws InterruptedException {
        Thread.sleep(1000);
        Assertions.assertThat(context).isEqualTo(CONTEXT);
        return TASK_1_RESULT;
    }

    @SneakyThrows
    @MethodTask
    public String normal2(TaskSupport taskSupport, String context) {
        Assertions.assertThat(context).isEqualTo(CONTEXT);

        /**
         * 获取 task1 的返回结果
         */
        String task1Result = (String) taskSupport.getResultMap().get("normal").getResult();
        Assertions.assertThat(task1Result).isEqualTo(TASK_1_RESULT);
        Thread.sleep(1000);
        return "task2";
    }

    @SneakyThrows
    @MethodTask(invoke = @Invoke(onFail = "demote", rollback = "Exception"), config = @MethodConfig(retryCount = 1))
    public void throwException() {
        System.out.println("will throwException");
        throw new RuntimeException("throwException test");
    }

    public void demote() {
        System.out.println("task2 execute fail");
    }

    @SneakyThrows
    @MethodTask
    public void timeout() {
        Thread.sleep(30000);
    }
}
