package com.gobrs.async.core.log;

import com.gobrs.async.core.common.util.SystemClock;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.gobrs.async.core.common.def.FixSave.LOGGER_PLUGIN;

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
        StringBuilder ms = printContent
                .append(String.format("%s【ProcessTrace】", LOGGER_PLUGIN ? "" : String.format("<%s>", logWrapper.getTraceId())))
                .append("Total cost: ")
                .append(SystemClock.now() - processStartTime)
                .append("ms")
                .append(" ");
        for (LogTracer tracer : logTracerList) {
            printContent = printContent
                    .append("【task】")
                    .append(tracer.getTaskName())
                    .append(" cost ")
                    .append(":")
                    .append(tracer.getTaskCost())
                    .append("ms");

            if (!tracer.getExecuteState()) {
                printContent.append("【state】：")
                        .append("fail")
                        .append("【errMsg】: ")
                        .append(tracer.getErrorMessage());
            } else {
                printContent.append("【state】：")
                        .append("success");
            }
            printContent.append("; ->");
        }
        if (StringUtils.isNotBlank(logWrapper.getStopTaskName())) {
            printContent.append("exec stopAsync task: ").append(logWrapper.getStopTaskName());
        }

        return printContent.substring(0, printContent.length() - 2);
    }

}
