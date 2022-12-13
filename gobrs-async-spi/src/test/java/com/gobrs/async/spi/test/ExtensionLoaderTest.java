package com.gobrs.async.spi.test;

import com.gobrs.async.spi.ExtensionLoader;
import com.gobrs.async.spi.test.portocol.Protocol;
import com.gobrs.async.spi.test.registry.RegistryActive;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The type Extension loader test.
 *
 * @program: gobrs -async
 * @ClassName ExtensionLoaderTest
 * @description:
 * @author: sizegang
 * @create: 2022 -12-13
 */
public class ExtensionLoaderTest {


    /**
     * Test register.
     */
    @Test
    public void testRegister() {
        RegistryActive registryActive = ExtensionLoader.getExtensionLoader(RegistryActive.class).getRealize("etcd");
        assertEquals(registryActive.register(), "etcd");
    }

    /**
     * Test extension class 1.
     */
    @Test
    public void testExtensionClass1() {
        Map<String, Class<?>> maps = ExtensionLoader.getExtensionLoader(RegistryActive.class).getRealizesMaps();
        System.out.println(maps);
    }

    /**
     * Protocol order.
     */
    @Test
    public void protocolOrder() {
        List<Protocol> realizes = ExtensionLoader.getExtensionLoader(Protocol.class).getRealizes();
        System.out.println(realizes);
    }


    /**
     * Protocol default.
     */
    @Test
    public void protocolDefault(){
        Protocol defaultRealize = ExtensionLoader.getExtensionLoader(Protocol.class).getDefaultRealize();

        assertEquals(defaultRealize.serialize(),"hession");
    }

}
