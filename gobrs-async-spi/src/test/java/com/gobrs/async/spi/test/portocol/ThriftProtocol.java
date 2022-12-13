package com.gobrs.async.spi.test.portocol;

import com.gobrs.async.spi.Realize;

/**
 * @program: gobrs-async
 * @ClassName ThriftProtocol
 * @description:
 * @author: sizegang
 * @create: 2022-12-13
 **/
@Realize(order = 2)
public class ThriftProtocol implements Protocol{

    @Override
    public String serialize() {
        return "thrift";
    }
}
