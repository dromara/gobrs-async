package com.jd.platform.async.wrapper;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 暂时用不上
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
