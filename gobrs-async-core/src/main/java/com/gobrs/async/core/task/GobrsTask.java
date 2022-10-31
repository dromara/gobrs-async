package com.gobrs.async.core.task;

import com.gobrs.async.core.TaskSupport;

/**
 * The interface Gobrs com.gobrs.async.com.gobrs.async.test.task.
 *
 * @param <Param>  the type parameter
 * @param <Result> the type parameter
 * @program: gobrs -async-core
 * @description:
 * @author: sizegang
 * @create: 2022 -03-23 23:31
 */
public interface GobrsTask<Param, Result> extends ITask {
    /**
     * Before the mission begins
     *
     * @param param the param
     */
    default void prepare(Param param) {
    }



    /**
     * Whether a com.gobrs.async.com.gobrs.async.test.task needs to be executed
     * <p>
     * The condition determines whether the com.gobrs.async.com.gobrs.async.test.task is executed or not.
     * This method is set as the default method because the user wants to return true by default, that is, the default selection is executed.
     *
     * @param param   the param
     * @param support the support
     * @return boolean boolean
     */
    default boolean nessary(Param param, TaskSupport support) {
        return true;
    }

    /**
     * Task Executed Successfully
     *
     * @param support the support
     */
    default void onSuccess(TaskSupport support) {
    }

    /**
     * Task execution failure
     *
     * @param support the support
     */
    default void onFail(TaskSupport support, Exception exception) {
    }


    /**
     * rollback
     * Rewrite the method to complete the com.gobrs.async.com.gobrs.async.test.task com.gobrs.async.com.gobrs.async.test.task Equivalent to TCC's two-phase submission transaction compensation
     *
     * @param param the param
     */
    default void rollback(Param param) {

    }
}
