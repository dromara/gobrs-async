package com.gobrs.async.util;

import com.gobrs.async.threadpool.GobrsThreadLocal;

/**
 * The type Trace util.
 *
 * @program: gobrs -async
 * @ClassName TraceUtil
 * @description:
 * @author: sizegang
 * @create: 2022 -10-09
 */
public class TraceUtil {

    /**
     * The constant gobrsThreadLocal.
     */
    public static GobrsThreadLocal gobrsThreadLocal = new GobrsThreadLocal();

    /**
     * Get object.
     *
     * @return the object
     */
    public static Object get() {
        return gobrsThreadLocal.get();
    }

    /**
     * Set.
     *
     * @param traceId the trace id
     */
    public static void set(long traceId) {
        gobrsThreadLocal.set(traceId);
    }
}
