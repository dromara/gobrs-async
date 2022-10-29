package com.gobrs.async.core.log;

import com.gobrs.async.core.task.AsyncTask;

import java.util.LinkedHashMap;


/**
 * The type Log wrapper.
 *
 * @program: gobrs -async
 * @ClassName LogWrapper
 * @description:
 * @author: sizegang
 * @create: 2022 -10-28
 */
public class LogWrapper {

    private Long traceId;

    private LinkedHashMap<AsyncTask, LogTracer> traceMap;

    /**
     * Gets trace map.
     *
     * @return the trace map
     */
    public LinkedHashMap<AsyncTask, LogTracer> getTraceMap() {
        return traceMap;
    }

    /**
     * Sets trace map.
     *
     * @param traceMap the trace map
     */
    public void setTraceMap(LinkedHashMap<AsyncTask, LogTracer> traceMap) {
        this.traceMap = traceMap;
    }

    /**
     * Instantiates a new Log wrapper.
     *
     * @param traceMap the trace map
     */
    public LogWrapper(LinkedHashMap<AsyncTask, LogTracer> traceMap) {
        this.traceMap = traceMap;
    }


    public Long getTraceId() {
        return traceId;
    }

    public void setTraceId(Long traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return "LogWrapper{" +
                "traceId=" + traceId +
                ", traceMap=" + traceMap +
                '}';
    }
}
