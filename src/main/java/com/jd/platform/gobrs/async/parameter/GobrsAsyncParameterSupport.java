package com.jd.platform.gobrs.async.parameter;

import com.jd.platform.gobrs.async.gobrs.GobrsAsync;
import com.jd.platform.gobrs.async.wrapper.TaskWrapper;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @program: gobrs-async
 * @ClassName GobrsParameterSupport
 * @description:
 * @author: sizegang
 * @create: 2022-01-27 02:32
 * @Version 1.0
 **/
public abstract class GobrsAsyncParameterSupport<P> implements GobrsAsync {

    public
    abstract void invoke(Supplier function);

}
