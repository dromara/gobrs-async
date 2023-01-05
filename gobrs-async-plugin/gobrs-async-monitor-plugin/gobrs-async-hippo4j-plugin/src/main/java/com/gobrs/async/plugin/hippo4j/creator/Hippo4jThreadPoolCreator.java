package com.gobrs.async.plugin.hippo4j.creator;

import com.gobrs.async.plugin.base.threadpool.ThreadPoolCreator;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * The type Hippo 4 j thread pool creator.
 *
 * @program: gobrs -async
 * @ClassName Hippo4jThreadPoolCreator
 * @description:
 * @author: sizegang
 * @create: 2023 -01-05
 */
public class Hippo4jThreadPoolCreator implements ThreadPoolCreator {

    private ThreadPoolExecutor hippo4ThreadPool;


    /**
     * Instantiates a new Hippo 4 j thread pool creator.
     *
     * @param hippo4ThreadPool the hippo 4 thread pool
     */
    public Hippo4jThreadPoolCreator(ThreadPoolExecutor hippo4ThreadPool) {
        this.hippo4ThreadPool = hippo4ThreadPool;
    }

    @Override
    public ThreadPoolExecutor create() {
        return hippo4ThreadPool;
    }
}
