package com.gobrs.async.core.task;

import com.gobrs.async.core.TaskActuator;
import com.gobrs.async.core.common.def.DefaultConfig;

import static com.gobrs.async.core.timer.Retry.retryConditional;

/**
 * The type Re using.
 *
 * @program: gobrs -async
 * @ClassName ReUsing
 * @description:
 * @author: sizegang
 * @create: 2022 -12-15
 */
public class ReUsing {

    /**
     * Reusing boolean.
     *
     * @param process the process
     * @return the boolean
     */
    public static boolean reusing(TaskActuator process) {
        return !retryConditional(process) && process.getTask().getTimeoutInMilliseconds() == DefaultConfig.TASK_TIME_OUT;
    }

}
