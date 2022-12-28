package com.gobrs.async.core.autoconfig;

import com.gobrs.async.core.common.def.FixSave;
import com.gobrs.async.plugin.base.wrapper.trace.TraceInterceptor;
import com.gobrs.async.spi.ExtensionLoader;

/**
 * The type Environment.
 *
 * @program: gobrs -async
 * @ClassName Environment
 * @description:
 * @author: sizegang
 * @create: 2022 -12-15
 */
public class Environment {

    /**
     * Env.
     */
    public static void env() {
        trace();
    }

    private static void trace() {
        TraceInterceptor realLizesFirst = ExtensionLoader.getExtensionLoader(TraceInterceptor.class).getRealLizesFirst();
        if (realLizesFirst == null) {
            return;
        }
        boolean sign = realLizesFirst.sign();
        if (sign) {
            FixSave.LOGGER_PLUGIN = true;
        }
    }

}
