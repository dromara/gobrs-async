package com.gobrs.async.threadpool;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: m-detail
 * @ClassName MDCThreadPoolExecutor
 * @description:
 * @author: sizegang
 * @create: 2022-04-23
 **/
public class MDCThreadPoolExecutor extends ThreadPoolExecutor {
    /**
     * Instantiates a new Mdc thread pool executor.
     *
     * @param corePoolSize  the core pool size
     * @param maxPoolSize   the max pool size
     * @param keepAliveTime the keep alive time
     * @param timeUnit      the time unit
     * @param workQueue     the work queue
     * @param caseReject    the case reject
     */
    public MDCThreadPoolExecutor(Integer corePoolSize, Integer maxPoolSize, Long keepAliveTime, TimeUnit timeUnit, BlockingQueue workQueue,
                                 RejectedExecutionHandler caseReject) {
        super(corePoolSize, maxPoolSize, keepAliveTime, timeUnit, workQueue, caseReject);
    }

    @Override
    public void execute(Runnable runnable) {
        // 获取父线程MDC中的内容，必须在run方法之前，否则等异步线程执行的时候有可能MDC里面的值已经被清空了，这个时候就会返回null
        final Map<String, String> context = MDC.getCopyOfContextMap();
        Runnable finalRunnable = runnable;
        super.execute(() -> {
            // 将父线程的MDC内容传给子线程
            MDC.setContextMap(context);
            try {
                // 执行异步操作
                finalRunnable.run();
            } finally {
                // 清空MDC内容
                MDC.clear();
            }
        });
    }
}
