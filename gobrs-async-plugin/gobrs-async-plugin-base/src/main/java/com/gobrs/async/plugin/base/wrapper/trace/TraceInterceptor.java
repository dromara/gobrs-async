package com.gobrs.async.plugin.base.wrapper.trace;

import com.gobrs.async.spi.SPI;

/**
 * The interface Trace interceptor.
 *
 * @program: gobrs -async
 * @ClassName TraceInterceptor
 * @description:
 * @author: sizegang
 * @create: 2022 -12-15
 */
@SPI
public interface TraceInterceptor {

    /**
     * Sign boolean.
     *
     * @return the boolean
     */
    boolean sign();

}
