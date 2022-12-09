package com.gobrs.async.core.common.util;

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
    public static boolean exceptionInterceptor(Exception exception) {
        if (exception instanceof InterruptedException) {
            return false;
        }
        return true;
    }

}
