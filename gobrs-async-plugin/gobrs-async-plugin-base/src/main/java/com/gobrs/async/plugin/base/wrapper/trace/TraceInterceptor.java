package com.gobrs.async.plugin.base.wrapper.trace;

import com.gobrs.async.spi.SPI;

/**
 * @program: gobrs-async
 * @ClassName TraceInterceptor
 * @description:
 * @author: sizegang
 * @create: 2022-12-15
 **/
@SPI
public interface TraceInterceptor {

    void trace();

}
