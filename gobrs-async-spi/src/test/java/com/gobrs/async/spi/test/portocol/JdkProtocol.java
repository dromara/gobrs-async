package com.gobrs.async.spi.test.portocol;

import com.gobrs.async.spi.Realize;

/**
 * @program: gobrs-async
 * @ClassName JdkProtocol
 * @description:
 * @author: sizegang
 * @create: 2022-12-13
 **/
@Realize(order = 4)
public class JdkProtocol implements Protocol{

    @Override
    public String serialize() {
        return "jdk";
    }
}
