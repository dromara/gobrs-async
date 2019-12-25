package com.tianyalei.async.group;


import com.tianyalei.async.callback.ICallback;
import com.tianyalei.async.callback.IWorker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 最终要执行时，都要放到一个group里，group集合会并发所有的Wrapper
 * @author wuweifeng wrote on 2019-11-19.
 */
public class WorkerGroup {
    private List<WorkerWrapper<?, ?>> workerWrapperList;
    //            +-----+       +-----+
    //   +------> | EH2 |-----> | EH3 |-------+
    //   |        +-----+       +-----+       |
    //   |                                    v
    //+-----+                               +-----+
    //| EH1 |                               | EH6 |
    //+-----+                               +-----+
    //   |                                    ^
    //   |        +-----+       +-----+       |
    //   +------> | EH4 |-----> | EH5 |-------+
    //            +-----+       +-----+
    /**
     * 如图，这种的begin就是EH1。begin，代表起始状态下，要并发执行的wrapper集合
     */
    private List<WorkerWrapper<?, ?>> beginList;

    public WorkerGroup() {
        workerWrapperList = new ArrayList<>();
    }

    /**
     * 起始任务
     */
    public WorkerGroup begin(WorkerWrapper<?, ?>... workerWrappers) {
        if (workerWrappers == null) {
            throw new NullPointerException("workerWrapper cannot be null");
        }
        beginList = Arrays.asList(workerWrappers);
        return this;
    }

    public WorkerGroup then(WorkerWrapper<?, ?>... workerWrappers) {
        if (workerWrappers == null) {
            throw new NullPointerException("workerWrapper cannot be null");
        }
        beginList = Arrays.asList(workerWrappers);
        return this;
    }

    /**
     * 添加需要串行执行的worker集合。一个wrapper可能只有一个worker，也可能是个要串行的worker集合
     *
     * @param workerWrapper workerWrapper
     */
    public WorkerGroup addWrapper(WorkerWrapper workerWrapper) {
        if (workerWrapper == null) {
            throw new NullPointerException("workerWrapper cannot be null");
        }
        workerWrapperList.add(workerWrapper);
        return this;
    }

    /**
     * 添加一个需要并行执行的worker
     *
     * @param iWorker iWorker
     */
    public <T, V> WorkerGroup addWrapper(IWorker<T, V> iWorker, T param, ICallback<T, V> iCallback) {
        synchronized (this) {
            WorkerWrapper<?, ?> workerWrapper = new WorkerWrapper<>(iWorker, param, iCallback);
            workerWrapperList.add(workerWrapper);
        }
        return this;
    }

    public WorkerGroup addWrappers(List<WorkerWrapper<?, ?>> workerWrappers) {
        if (workerWrappers == null) {
            throw new NullPointerException("workers cannot be null");
        }
        this.workerWrapperList.addAll(workerWrappers);
        return this;
    }

    public WorkerGroup addWrappers(WorkerWrapper<?, ?>... workerWrappers) {
        if (workerWrappers == null) {
            throw new NullPointerException("workers cannot be null");
        }
        return addWrappers(Arrays.asList(workerWrappers));
    }

    /**
     * 添加一个不需要回调的worker
     *
     * @param iWorker async.worker
     */
    public <T, V> WorkerGroup addWrapper(IWorker<T, V> iWorker, T param) {
        return this.addWrapper(iWorker, param, null);
    }

    /**
     * 添加一个不需要回调的worker
     *
     * @param iWorker async.worker
     */
    public <T, V> WorkerGroup addWrapper(IWorker<T, V> iWorker) {
        return this.addWrapper(iWorker, null);
    }

    /**
     * 返回当前worker的数量，用于决定启用的线程数量
     *
     * @return size
     */
    public int size() {
        synchronized (this) {
            return workerWrapperList.size();
        }
    }

    public List<WorkerWrapper<?, ?>> getWorkerWrapperList() {
        return workerWrapperList;
    }
}
