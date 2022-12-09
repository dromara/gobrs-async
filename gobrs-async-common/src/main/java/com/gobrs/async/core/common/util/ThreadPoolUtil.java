package com.gobrs.async.core.common.util;

import java.math.BigDecimal;

/**
 * The type Thread pool util.
 *
 * @program: gobrs -async
 * @ClassName ThreadPoolUtil
 * @description:
 * @author: sizegang
 * @create: 2022 -12-08
 */
public class ThreadPoolUtil {

    public static Integer calculateCoreNum() {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        return new BigDecimal(cpuCoreNum).divide(new BigDecimal("0.2")).intValue();
    }

}
