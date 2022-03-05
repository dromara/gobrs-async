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

    public static class GobrsState {
        public GobrsState(Integer state) {
            this.state = state;
        }

        private int state;
        private int expCode;

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }

        public int getExpCode() {
            return expCode;
        }

        public void setExpCode(int expCode) {
            this.expCode = expCode;
        }
    }


}
