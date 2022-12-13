package com.gobrs.async.spi.test.portocol;

import com.gobrs.async.spi.Realize;

/**
 * @program: gobrs-async
 * @ClassName KryroProtocol
 * @description:
 * @author: sizegang
 * @create: 2022-12-13
 **/
@Realize(order = 3)
public class KryroProtocol  implements Protocol{

    @Override
    public String serialize() {
        return "kryro";
    }
}
