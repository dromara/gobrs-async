package com.gobrs.async.core.common.def;

import org.apache.logging.log4j.util.Strings;

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
    int RETRY_COUNT = 0;
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
    String QUEUE_SIZE = Strings.EMPTY;

    /**
     * The constant TASK_TIME_OUT.
     */
    int TASK_TIME_OUT = 0;

    /**
     * The constant ERR_LOGABLED.
     */
    boolean ERR_LOGABLED = true;

    /**
     * The constant COST_LOGABLED.
     */
    boolean COST_LOGABLED = true;


    /**
     * The constant TASK_INITIALIZE.
     */
    int TASK_INITIALIZE = 0;

    /**
     * The constant TASK_FINISH.
     */
    int TASK_FINISH = Integer.MAX_VALUE;

    /**
     * The constant TASK_TIMEOUT.
     */
    int TASK_TIMEOUT = -1;

}
