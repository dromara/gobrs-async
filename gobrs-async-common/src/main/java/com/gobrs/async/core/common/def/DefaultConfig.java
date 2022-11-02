package com.gobrs.async.core.common.def;

/**
 * The interface Default com.gobrs.async.config.
 *
 * @author : wh
 * @date : 2022/6/1 11:39
 * @description:
 */
public interface DefaultConfig {

    /**
     * The constant TASKNAME.
     */
    String TASKNAME = "asyncTaskName";

    /**
     * The constant THREADPOOLQUEUESIZE.
     */
    Integer THREADPOOLQUEUESIZE = 10000;

    /**
     * The constant KEEPALIVETIME.
     */
    Long KEEPALIVETIME = 30000L;

    /**
     * The constant EXECUTETIMEOUT.
     */
    Long EXECUTETIMEOUT = 10000L;

    /**
     * The constant RULE_ANY.
     */
    String RULE_ANY = "any";

    /**
     * The constant RULE_ANY_CONDITION.
     */
    String RULE_ANY_CONDITION = "anyCondition";

    /**
     * The constant RULE_EXCLUSIVE.
     */
    String RULE_EXCLUSIVE = "exclusive";

    /**
     * The constant retryCount.
     */
    int retryCount = 1;
    /**
     * Whether to execute a subtask if it fails
     */
    boolean failSubExec = false;
    /**
     * Transaction com.gobrs.async.com.gobrs.async.test.task
     */
    boolean transaction = false;

    /**
     * The constant anyConditionState.
     */
    boolean anyConditionState = true;


    /**
     * The constant CORE_SIZE.
     */
    Integer CORE_SIZE = 100;

    /**
     * The constant MAX_SIZE.
     */
    Integer MAX_SIZE = 200;

    /**
     * The constant REJECT.
     */
    String REJECT = "AbortPolicy";
    /**
     * The constant QUEUE.
     */
    String QUEUE_SIZE = "";


}
