package com.gobrs.async.plugin.trace;

import com.yomahub.tlog.core.enhance.bytes.AspectLogEnhance;

/**
 * The type Gobrs logger.
 *
 * @program: gobrs -async
 * @ClassName GobrsLogger
 * @description:
 * @author: sizegang
 * @create: 2022 -12-15
 */
public class GobrsLogger {


    /**
     * Logger.
     */
    public static void logger(){
        AspectLogEnhance.enhance();
    }

}
