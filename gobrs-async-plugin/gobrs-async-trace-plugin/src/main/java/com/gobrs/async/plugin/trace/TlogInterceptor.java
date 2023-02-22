package com.gobrs.async.plugin.trace;

import com.gobrs.async.plugin.base.trace.TraceInterceptor;
import com.gobrs.async.spi.Realize;

/**
 * The type Tlog interceptor.
 *
 * @program: gobrs -async
 * @ClassName TlogInterceptor
 * @description:
 * @author: sizegang
 * @create: 2022 -12-15
 */
@Realize
public class TlogInterceptor implements TraceInterceptor {

    @Override
    public boolean sign() {
        return true;
    }
}
