package com.gobrs.async.core.log;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * The type Log wrapper.
 *
 * @program: gobrs -async
 * @ClassName LogWrapper
 * @description:
 * @author: sizegang
 * @create: 2022 -10-28
 */
@Accessors(chain = true)
@Data
public class LogWrapper {

    private Long traceId;

    private final LinkedBlockingQueue<LogTracer> tracerQueue = new LinkedBlockingQueue<>();

    private TimeCollector timeCollector;

    private Long processCost;

    /**
     * 流程执行中断的任务名称
     */
    private String stopTaskName;


    /**
     * Add trace.
     *
     * @param logTracer the log tracer
     */
    public void addTrace(LogTracer logTracer) {
        tracerQueue.add(logTracer);
    }

    /**
     * The type Time collector.
     */
    @Builder
    @Data
    @ToString
    public static class TimeCollector {

        private Long startTime;

        private Long endTime;
    }
}
