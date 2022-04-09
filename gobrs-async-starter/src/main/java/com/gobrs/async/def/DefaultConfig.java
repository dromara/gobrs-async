package com.gobrs.async.def;

/**
 * @program: gobrs-async-core
 * @ClassName DefaultConfig
 * @description:
 * @author: sizegang
 * @create: 2022-03-26
 **/
public class DefaultConfig {
    public static final String TASKNAME = "asyncTaskName";
    public static final Integer THREADPOOLQUEUESIZE = 10000;
    public static final Long KEEPALIVETIME = 30000L;
    public static final Long EXECUTETIMEOUT = 10000L;
    public static final int retryCount = 1;
    /**
     * Whether to execute a subtask if it fails
     */
    public static final boolean failSubExec = false;
    /**
     * Transaction task
     */
    public static final boolean transaction = false;
}
