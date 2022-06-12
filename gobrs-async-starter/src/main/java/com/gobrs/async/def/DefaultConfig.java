package com.gobrs.async.def;

/**
 * @author : wh
 * @date : 2022/6/1 11:39
 * @description:
 */
public interface DefaultConfig {

    String TASKNAME = "asyncTaskName";

    Integer THREADPOOLQUEUESIZE = 10000;

    Long KEEPALIVETIME = 30000L;

    Long EXECUTETIMEOUT = 10000L;

    String RULE_ANY = "any";
    String RULE_EXCLUSIVE = "exclusive";

    int retryCount = 1;
    /**
     * Whether to execute a subtask if it fails
     */
    boolean failSubExec = false;
    /**
     * Transaction task
     */
    boolean transaction = false;


}
