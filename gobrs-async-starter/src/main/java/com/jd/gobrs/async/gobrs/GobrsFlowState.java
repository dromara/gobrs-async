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
    public static Map<Long, AtomicInteger> gobrsFlowState = new ConcurrentHashMap<>();
    public static final int FINISH = 2;
    public static final int ERROR = 1;
    public static final int WORKING = 0;

    public static boolean compareAndSetState(int expect, int update, long businessId) {
        AtomicInteger st = gobrsFlowState.get(businessId);
        if (st == null) {
            st = new AtomicInteger(WORKING);
            boolean b = st.compareAndSet(expect, update);
            gobrsFlowState.put(businessId, st);
            return b;
        }
        return st.compareAndSet(expect, update);
    }

}
