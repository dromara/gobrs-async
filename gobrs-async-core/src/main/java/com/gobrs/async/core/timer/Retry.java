package com.gobrs.async.core.timer;

import com.gobrs.async.core.TaskActuator;
import com.gobrs.async.core.common.def.DefaultConfig;

/**
 * The type Retry util.
 *
 * @program: gobrs -async
 * @ClassName RetryUtil
 * @description:
 * @author: sizegang
 * @create: 2022 -12-13
 */
public class Retry {

    /**
     * Retry conditional boolean.
     *
     * @param taskActuator the task actuator
     * @return the boolean
     */
    public static boolean retryConditional(TaskActuator<?> taskActuator) {
        return taskActuator.getTask().getRetryCount() > DefaultConfig.RETRY_COUNT;
    }
}
