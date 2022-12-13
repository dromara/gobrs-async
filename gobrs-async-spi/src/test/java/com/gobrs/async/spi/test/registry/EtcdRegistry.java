package com.gobrs.async.spi.test.registry;

import com.gobrs.async.spi.Realize;

/**
 * @program: gobrs-async
 * @ClassName EtcdRegistry
 * @description:
 * @author: sizegang
 * @create: 2022-12-13
 **/
@Realize
public class EtcdRegistry implements RegistryActive{

    @Override
    public String register() {
        return "etcd";
    }
}
