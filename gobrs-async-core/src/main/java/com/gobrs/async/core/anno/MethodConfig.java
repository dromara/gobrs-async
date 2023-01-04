package com.gobrs.async.core.anno;

import com.gobrs.async.core.common.def.DefaultConfig;
import org.apache.logging.log4j.util.Strings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: gobrs-async
 * @ClassName Config
 * @description:
 * @author: sizegang
 * @create: 2023-01-04
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface MethodConfig {

    /**
     * Retry times
     *
     * @return int int
     */
    int retryCount() default DefaultConfig.RETRY_COUNT;

    /**
     * 执行服务节点时允许的最大等待时间。默认为 -1 代表不设置超时时间，一直等待
     *
     * @return 超时时间 ，单位：ms
     */
    int timeoutInMilliseconds() default DefaultConfig.TASK_TIME_OUT;

    /**
     * com.gobrs.async.com.gobrs.async.test.task  name
     *
     * @return string string
     */
    String desc() default Strings.EMPTY;

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
}
