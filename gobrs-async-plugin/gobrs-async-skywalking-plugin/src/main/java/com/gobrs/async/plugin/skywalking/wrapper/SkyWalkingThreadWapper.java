package com.gobrs.async.plugin.skywalking.wrapper;

import com.gobrs.async.plugin.base.wrapper.ThreadWapper;
import com.gobrs.async.spi.Realize;
import org.apache.skywalking.apm.toolkit.trace.CallableWrapper;

import java.util.concurrent.Callable;

/**
 * The type Sky walking thread wapper.
 *
 * @param <T> the type parameter
 * @program: gobrs -async
 * @ClassName SkyWalkingThreadWapper
 * @description:
 * @author: sizegang
 * @create: 2022 -12-13
 */
@Realize
public class SkyWalkingThreadWapper<T> implements ThreadWapper<T> {

    @Override
    public Callable<T> wrapper(Callable<T> callable) {
        return CallableWrapper.of(callable);
    }
}
