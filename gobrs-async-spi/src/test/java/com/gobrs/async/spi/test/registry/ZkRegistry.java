package com.gobrs.async.spi.test.registry;

import com.gobrs.async.spi.Realize;

/**
 * The type Zk registry.
 *
 * @program: gobrs -async
 * @ClassName ZkRegistry
 * @description:
 * @author: sizegang
 * @create: 2022 -12-13
 */
@Realize
public class ZkRegistry implements RegistryActive{

    @Override
    public String register() {
        return "zk";
    }
}
