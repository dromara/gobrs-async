package com.gobrs.async.spi.test.registry;

import com.gobrs.async.spi.Realize;

/**
 * @program: gobrs-async
 * @ClassName NacosRegistry
 * @description:
 * @author: sizegang
 * @create: 2022-12-13
 **/
@Realize(order = 10)
public class NacosRegistry implements RegistryActive{

    @Override
    public String register() {

        return "nacos";
    }
}
