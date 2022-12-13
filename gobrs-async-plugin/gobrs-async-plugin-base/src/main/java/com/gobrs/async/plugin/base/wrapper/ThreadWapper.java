package com.gobrs.async.plugin.base.wrapper;

import com.gobrs.async.spi.SPI;

import java.util.concurrent.Callable;

/**
 * @program: gobrs-async
 * @ClassName test
 * @description:
 * @author: sizegang
 * @create: 2022-12-13
 **/
@SPI
public interface ThreadWapper<T> {

    /**
     * Wrapper callable.
     *
     * @param callable the callable
     * @return the callable
     */
    Callable<T> wrapper(Callable<T> callable);

}