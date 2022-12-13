package com.gobrs.async.spi.test.portocol;

import com.gobrs.async.spi.Realize;

/**
 * The type Hession protocol.
 *
 * @program: gobrs -async
 * @ClassName HessionProtocol
 * @description:
 * @author: sizegang
 * @create: 2022 -12-13
 */
@Realize(order = 1)
public class HessionProtocol implements  Protocol{

    @Override
    public String serialize() {
        return "hession";
    }
}
