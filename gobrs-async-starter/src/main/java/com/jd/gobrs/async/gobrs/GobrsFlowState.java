package com.jd.gobrs.async.gobrs;

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
    public static Map<Long, Integer> gobrsFlowState = new ConcurrentHashMap<>();
    public static final int FINISH = 2;
    public static final int ERROR = 1;
    public static final int WORKING = 0;

    public static boolean compareAndSetState(int expect, int update, long businessId) {
        Integer st = gobrsFlowState.get(businessId);
        if (st == null) {
            st = WORKING;
            if (expect == st) {
                st = update;
                gobrsFlowState.put(businessId, st);
                return true;
            }
            return false;
        }
        if (expect == st) {
            st = update;
            gobrsFlowState.put(businessId, st);
            return true;
        }
        return false;
    }

}
