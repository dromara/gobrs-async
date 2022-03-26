package com.gobrs.async.task;

import com.gobrs.async.TaskSupport;

/**
 * @program: gobrs-async-core
 * @description:
 * @author: sizegang
 * @create: 2022-03-23 23:31
 **/
public interface GobrsTask<Param, Result> extends Task {
    /**
     * Before the mission begins
     *
     * @param param
     */
    void prepare(Param param);

    /**
     * Tasks to be performed
     *
     * @param param
     * @return
     */
    Result task(Param param, TaskSupport support);

    /**
     * Whether a task needs to be executed
     *
     * @param param
     * @return
     */
    boolean nessary(Param param, TaskSupport support);

    /**
     * Task Executed Successfully
     *
     * @param support
     */
    void onSuccess(TaskSupport support);

    /**
     * Task execution failure
     *
     * @param support
     */
    void onFail(TaskSupport support);

    /**
     * rollback
     */
    default void rollback(Param param) {

    }
}
