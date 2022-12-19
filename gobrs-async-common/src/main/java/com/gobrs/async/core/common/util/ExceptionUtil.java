package com.gobrs.async.core.common.util;

import com.gobrs.async.core.common.exception.AsyncTaskTimeoutException;

import static com.gobrs.async.core.common.def.DefaultConfig.TASK_TIMEOUT;

/**
 * The type Exception util.
 *
 * @program: gobrs -async
 * @ClassName ExceptionUtil
 * @description:
 * @author: sizegang
 * @create: 2022 -12-09
 */
public class ExceptionUtil {
    /**
     * Exception interceptor boolean.
     *
     * @param exception the exception
     * @return the boolean
     */
    public static boolean excludeInterceptException(Exception exception) {
        if (exception instanceof InterruptedException) {
            return false;
        }
        return true;
    }

}
