package com.gobrs.async.core.log;

import lombok.Builder;
import lombok.Data;

/**
 * The type Log tracer.
 *
 * @program: gobrs -async
 * @ClassName LogTracer
 * @description:
 * @author: sizegang
 * @create: 2022 -10-28
 */
@Builder
@Data
public class LogTracer {


    /**
     * 任务执行时间
     */
    private Long taskCost;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务昵称
     */
    private String nickName;

    /**
     * 任务异常错误信息
     */
    private String errorMessage;

    /**
     * 任务执行状态
     */
    private Boolean executeState;
}
