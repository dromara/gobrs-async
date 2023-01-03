package com.gobrs.async.core.property;


import lombok.Data;


/**
 * The type Rule.
 *
 * @author sizegang1
 * @program: gobrs -async
 * @ClassName Rule
 * @description:
 * @author: sizegang
 * @create: 2022 -01-26 01:39
 * @Version 1.0
 * @date 2022 -01-27
 */
@Data
public class RuleConfig {

    private String name;

    private String content;

    private LogConfig logConfig;

    private boolean catchable;

    private GobrsAsyncProperties.ThreadPool threadPool;

    /**
     * Whether the execution com.gobrs.async.exception interrupts the workflow
     * 任务流程 某任务中断是否终止整个任务流程
     */
    private boolean taskInterrupt = false;

    private boolean interruptionImmediate = true;

    /**
     * 流程事务
     */
    boolean transaction = false;
}
