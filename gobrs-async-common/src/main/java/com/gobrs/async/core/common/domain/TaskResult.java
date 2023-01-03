package com.gobrs.async.core.common.domain;

import com.gobrs.async.core.common.enums.ResultState;
import lombok.Data;

/**
 * Single com.gobrs.async.com.gobrs.async.test.task execution result encapsulation
 * 任务结果封装
 *
 * @param <V> the type parameter
 */
@Data
public class TaskResult<V> {
    /**
     * The execution result
     */
    private V result;
    /**
     * result status
     */
    private ResultState resultState;

    /**
     * The execution com.gobrs.async.exception
     */
    private Exception ex;

    /**
     * Instantiates a new Task result.
     *
     * @param result      the result
     * @param resultState the result state
     */
    public TaskResult(V result, ResultState resultState) {
        this(result, resultState, null);
    }

    /**
     * Instantiates a new Task result.
     *
     * @param result      the result
     * @param resultState the result state
     * @param ex          the ex
     */
    public TaskResult(V result, ResultState resultState, Exception ex) {
        if (result instanceof AnyConditionResult) {
            this.result = (V) ((AnyConditionResult<?>) result).getResult();
        } else {
            this.result = result;
        }
        this.resultState = resultState;
        this.ex = ex;
    }


}
