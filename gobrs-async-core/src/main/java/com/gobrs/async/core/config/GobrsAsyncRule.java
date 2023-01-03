package com.gobrs.async.core.config;

import com.gobrs.async.core.common.def.DefaultConfig;
import lombok.Data;

/**
 * The type Gobrs async rule.
 *
 * @program: gobrs -async
 * @ClassName GobrsAsyncRule
 * @description:
 * @author: sizegang
 * @create: 2022 -12-10
 */
@Data
public class GobrsAsyncRule {

    private String name;

    private String content;


    /**
     * 执行异常trace log打印
     */
    private Boolean errLogabled = DefaultConfig.ERR_LOGABLED;
    /**
     * 任务执行过程中耗时打印
     */
    private Boolean costLogabled = DefaultConfig.COST_LOGABLED;

    private GobrsConfig.ThreadPool threadPool;


    /**
     * Whether the execution com.gobrs.async.exception interrupts the workflow
     * 任务流程 某任务中断是否终止整个任务流程
     */
    private boolean taskInterrupt = false;

    private boolean interruptionImmediate = true;

    private boolean catchable = false;

    /**
     * 流程事务
     */
    boolean transaction = false;
}
