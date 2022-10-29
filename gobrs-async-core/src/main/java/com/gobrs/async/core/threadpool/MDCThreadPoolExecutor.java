package com.gobrs.async.core.threadpool;

import org.slf4j.MDC;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The type Mdc thread pool executor.
 * com.gobrs.async.log 跨线程传递
 * @program: m -detail
 * @ClassName MDCThreadPoolExecutor
 * @description:
 * @author: sizegang
 * @create: 2022 -04-23
 */
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
        /**
         * The content in the MDC of the parent thread must be obtained before the run method,
         * otherwise the value in the MDC may have been cleared when the asynchronous thread is executed, and null will be returned at this time.
         */
        final Map<String, String> context = MDC.getCopyOfContextMap();

        Runnable finalRunnable = runnable;

        super.execute(() -> {
            /**
             * Pass the MDC content of the parent thread to the child thread
             */
            if(Objects.nonNull(context)){
                MDC.setContextMap(context);
            }
            try {
                /**
                 * perform asynchronous operations
                 */
                finalRunnable.run();
            } finally {
                /**
                 * clear
                 */
                MDC.clear();
            }
        });
    }
}
