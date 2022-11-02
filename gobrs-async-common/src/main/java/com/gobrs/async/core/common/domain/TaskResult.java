package com.gobrs.async.core.common.domain;

import com.gobrs.async.core.common.enums.ResultState;

/**
 * Single com.gobrs.async.com.gobrs.async.test.task execution result encapsulation
 * 任务结果封装
 *
 * @param <V> the type parameter
 */
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

    /**
     * Default result com.gobrs.async.com.gobrs.async.test.task result.
     *
     * @param <V> the type parameter
     * @return the com.gobrs.async.com.gobrs.async.test.task result
     */
    public static <V> TaskResult<V> defaultResult() {
        return new TaskResult<>(null, ResultState.DEFAULT);
    }

    @Override
    public String toString() {
        return "WorkResult{" +
                "result=" + result +
                ", resultState=" + resultState +
                ", ex=" + ex +
                '}';
    }

    /**
     * Gets ex.
     *
     * @return the ex
     */
    public Exception getEx() {
        return ex;
    }

    /**
     * Sets ex.
     *
     * @param ex the ex
     */
    public void setEx(Exception ex) {
        this.ex = ex;
    }

    /**
     * Gets result.
     *
     * @return the result
     */
    public V getResult() {
        return result;
    }

    /**
     * Sets result.
     *
     * @param result the result
     */
    public void setResult(V result) {
        this.result = result;
    }

    /**
     * Gets result state.
     *
     * @return the result state
     */
    public ResultState getResultState() {
        return resultState;
    }

    /**
     * Sets result state.
     *
     * @param resultState the result state
     */
    public void setResultState(ResultState resultState) {
        this.resultState = resultState;
    }
}
