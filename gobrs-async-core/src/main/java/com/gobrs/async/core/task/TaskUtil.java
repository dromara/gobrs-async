package com.gobrs.async.core.task;

import com.gobrs.async.core.common.domain.AnyConditionResult;
import com.gobrs.async.core.common.domain.TaskResult;
import com.gobrs.async.core.common.enums.ResultState;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * The type Task com.gobrs.async.util.
 *
 * @program: gobrs -async
 * @ClassName TaskUtil
 * @description:
 * @author: sizegang
 * @create: 2022 -09-28
 */
public class TaskUtil<Param,Result> {

    /**
     * Multiple dependencies boolean.
     *
     * @param upwardTasksMap the upward tasks map
     * @param subTasks       the sub tasks
     * @return the boolean
     */
    public static boolean multipleDependencies(Map<AsyncTask<?,?>, List<AsyncTask<?,?>>> upwardTasksMap, List<AsyncTask<?,?>> subTasks) {
        for (AsyncTask<?,?> subTask : subTasks) {
            if (!CollectionUtils.isEmpty(upwardTasksMap.get(subTask)) && upwardTasksMap.get(subTask).size() > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Default any condition any condition.
     *
     * @return the any condition
     */
    public static AnyConditionResult defaultAnyCondition() {
        return AnyConditionResult.builder().state(false).build();
    }

    /**
     * Default any condition any condition.
     *
     * @param state the state
     * @return the any condition
     */
    public static AnyConditionResult defaultAnyCondition(boolean state) {
        return AnyConditionResult.builder().state(state).build();
    }

    public static TaskResult buildFutureTaskResult(Future result) {
        return new TaskResult(result, ResultState.SUCCESS, null);
    }

}
