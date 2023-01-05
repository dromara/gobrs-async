package com.gobrs.async.core.task;

import com.gobrs.async.core.TaskSupport;
import lombok.Builder;
import lombok.Data;

/**
 * The type M task context.
 *
 * @param <Param> the type parameter
 * @program: gobrs -async
 * @ClassName MTaskContext
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
@Builder
@Data
public class MTaskContext<Param> {

    /**
     * The Support.
     */
    TaskSupport support;

    /**
     * The Param.
     */
    Param param;


    /**
     * Gets param.
     *
     * @return the param
     */
    public Param getParam() {
        return param;
    }

    /**
     * Gets task result.
     *
     * @param <T>      the type parameter
     * @param taskName the task name
     * @param clazz    the clazz
     * @return the task result
     */
    public <T> T getTaskResult(String taskName, Class<T> clazz) {
        return (T) support.getResultMap().get(taskName).getResult();
    }

}
