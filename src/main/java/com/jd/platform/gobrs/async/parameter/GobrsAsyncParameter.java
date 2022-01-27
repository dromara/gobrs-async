package com.jd.platform.gobrs.async.parameter;

import java.util.function.Supplier;

/**
 * @program: gobrs-async
 * @ClassName GobrsAsyncParameter
 * @description:
 * @author: sizegang
 * @create: 2022-01-27 02:39
 * @Version 1.0
 **/
public class GobrsAsyncParameter extends GobrsAsyncParameterSupport {

    @Override
    public void invoke(Supplier consumer) {
        consumer.get();
    }

}
