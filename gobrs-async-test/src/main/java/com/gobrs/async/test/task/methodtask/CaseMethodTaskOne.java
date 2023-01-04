package com.gobrs.async.test.task.methodtask;

import com.gobrs.async.core.anno.Invoke;
import com.gobrs.async.core.anno.MethodComponent;
import com.gobrs.async.core.anno.MethodTask;

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
    @MethodTask(name = "case1", invoke = @Invoke(task = "case1"))
    public void case1() {
        System.out.println("case1");
    }

    /**
     * Case 2.
     */
    @MethodTask(name = "case2", invoke = @Invoke(task = "case2"))
    public void case2() {
        System.out.println("case2");
    }


}
