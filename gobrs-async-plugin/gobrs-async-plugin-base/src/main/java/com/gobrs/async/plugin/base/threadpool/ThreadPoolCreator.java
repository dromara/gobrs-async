package com.gobrs.async.plugin.base.threadpool;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * The interface Thread pool creator.
 *
 * @program: gobrs -async
 * @ClassName ThreadPoolCreator
 * @description:
 * @author: sizegang
 * @create: 2023 -01-05
 */
public interface ThreadPoolCreator {

    /**
     * Create thread pool executor.
     *
     * @return the thread pool executor
     */
    ThreadPoolExecutor create();
}
