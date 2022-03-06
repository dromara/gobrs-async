package com.jd.gobrs.async.gobrs;

import com.jd.gobrs.async.task.TaskResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: gobrs-async
 * @ClassName GobrsAsyncParams
 * @description:
 * @author: sizegang
 * @create: 2022-03-05
 **/
public class GobrsAsyncSupport<V> {

    // 创建task对象存储
    private volatile Map<String, TaskResult<V>> workResult;


    // taskFlow 任务流状态
    private volatile AtomicInteger taskFlowState;


    private Map<String, Object> params;// 任务参数


    private Integer expCode; // 任务流异常状态码

    /**
     * 标记该事件是否已经被处理过了，譬如已经超时返回false了，后续rpc又收到返回值了，则不再二次回调
     * 经试验,volatile并不能保证"同一毫秒"内,多线程对该值的修改和拉取
     * <p>
     * 1-finish, 2-error, 3-working
     */
    public volatile Map<String, AtomicInteger> taskState;


    public static SupportBuilder builder() {
        return new SupportBuilder();
    }

    // 属性值都是取的 Builder中的值
    public GobrsAsyncSupport(SupportBuilder builder) {
        this.workResult = builder.workResult;
        this.taskState = builder.taskState;
        this.taskFlowState = builder.taskFlowState;
        this.expCode = builder.expCode;
        this.params = builder.params;
    }


    public Map<String, TaskResult<V>> getWorkResult() {
        return workResult;
    }

    public AtomicInteger getTaskState(String id) {
        return taskState.get(id);
    }

    public AtomicInteger getTaskFlowState() {
        return taskFlowState;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public Integer getExpCode() {
        return expCode;
    }

    public void setWorkResult(Map<String, TaskResult<V>> workResult) {
        this.workResult = workResult;
    }

    public void setTaskFlowState(AtomicInteger taskFlowState) {
        this.taskFlowState = taskFlowState;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setExpCode(Integer expCode) {
        this.expCode = expCode;
    }

    public void setTaskState(Map<String, AtomicInteger> taskState) {
        this.taskState = taskState;
    }

    public static class SupportBuilder<V> {

        // 创建task对象存储
        private volatile Map<String, TaskResult<V>> workResult = new ConcurrentHashMap<String, TaskResult<V>>();


        // taskFlow 任务流状态
        private volatile AtomicInteger taskFlowState = new AtomicInteger();

        Map<String, Object> params;// 任务参数

        private Integer expCode; // 任务流异常状态码

        /**
         * 标记该事件是否已经被处理过了，譬如已经超时返回false了，后续rpc又收到返回值了，则不再二次回调
         * 经试验,volatile并不能保证"同一毫秒"内,多线程对该值的修改和拉取
         * <p>
         * 1-finish, 2-error, 3-working
         */
        public volatile Map<String, Integer> taskState = new ConcurrentHashMap<>();


        public SupportBuilder workResult(Map<String, TaskResult<V>> workResult) {
            this.workResult = workResult;
            return this;
        }


        public SupportBuilder taskFlowState(AtomicInteger taskFlowState) {
            this.taskFlowState = taskFlowState;
            return this;
        }

        public SupportBuilder params(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public SupportBuilder expCode(Integer expCode) {
            this.expCode = expCode;
            return this;
        }

        public SupportBuilder taskState(ConcurrentHashMap<String, Integer> taskState) {
            this.taskState = taskState;
            return this;
        }


        public GobrsAsyncSupport build() {
            return new GobrsAsyncSupport(this);
        }
    }


}
