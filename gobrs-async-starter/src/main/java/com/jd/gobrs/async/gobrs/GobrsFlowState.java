package com.jd.gobrs.async.gobrs;

import com.jd.gobrs.async.constant.StateConstant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: gobrs-async
 * @ClassName GobrsFlowState
 * @description:
 * @author: sizegang
 * @create: 2022-02-19 23:20
 * @Version 1.0
 **/
public class GobrsFlowState {

    public static boolean compareAndSetState(AtomicInteger taskFlowState, int expect, int update) {
        return taskFlowState.compareAndSet(expect, update);
    }

    public static boolean assertState(AtomicInteger taskFlowState, int expect) {
        return taskFlowState.get() == expect;
    }

}
