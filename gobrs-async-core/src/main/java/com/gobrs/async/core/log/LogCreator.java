package com.gobrs.async.core.log;

import com.gobrs.async.core.common.util.SystemClock;

import java.util.*;

/**
 * The type Log creator.
 *
 * @program: gobrs -async
 * @ClassName LogCreator
 * @description:
 * @author: sizegang
 * @create: 2022 -10-28
 */
public class LogCreator {

    /**
     * Create com.gobrs.async.com.gobrs.async.test.task com.gobrs.async.log string.
     *
     * @param logTracer the com.gobrs.async.log tracer
     * @return the string
     */
    public String createTaskLog(LogTracer logTracer) {
        // todo 构建每个任务的日志信息
        return null;

    }

    /**
     * Process logs string
     *
     * @param logWrapper the log wrapper
     * @return the string
     */
    public static String processLogs(LogWrapper logWrapper) {
        StringBuilder printContent = new StringBuilder();
        List<LogTracer> logTracerList = new ArrayList<>();
        Long processStartTime = logWrapper.getTimeCollector().getStartTime();
        logWrapper.getTracerQueue().drainTo(logTracerList);
        printContent.append("任务总耗时: ")
                .append(SystemClock.now() - processStartTime)
                .append("ms")
                .append(" | ")
                .append("traceId = ")
                .append(logWrapper.getTraceId())
                .append(" | ");
        for (LogTracer tracer : logTracerList) {
            printContent
                    .append("【")
                    .append(tracer.getTaskName())
                    .append("】")
                    .append(":")
                    .append(tracer.getTaskCost())
                    .append("ms")
                    .append("; =>");
        }

        return printContent.substring(0, printContent.length() - 2);
    }

}
