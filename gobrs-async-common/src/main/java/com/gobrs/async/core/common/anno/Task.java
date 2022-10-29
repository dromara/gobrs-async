package com.gobrs.async.core.common.anno;

import com.gobrs.async.core.common.def.DefaultConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Task.
 *
 * @author sizegang1
 * @program: gobrs -async-core
 * @author: sizegang
 * @date 2022 -04-07
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Task {

    /**
     * com.gobrs.async.com.gobrs.async.test.task  name
     *
     * @return string string
     */
    String desc() default DefaultConfig.TASKNAME;

    /**
     * Transaction com.gobrs.async.com.gobrs.async.test.task
     *
     * @return boolean boolean
     */
    boolean callback() default false;

    /**
     * Whether to continue executing subtasks after a com.gobrs.async.com.gobrs.async.test.task fails
     *
     * @return boolean boolean
     */
    boolean failSubExec() default false;

    /**
     * Retry times
     *
     * @return int int
     */
    int retryCount() default DefaultConfig.retryCount;


}
