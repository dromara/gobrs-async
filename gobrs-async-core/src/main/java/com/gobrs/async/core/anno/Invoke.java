package com.gobrs.async.core.anno;

import com.gobrs.async.core.common.def.DefaultConfig;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Invoke.
 *
 * @program: gobrs -async
 * @ClassName Invoke
 * @description:
 * @author: sizegang
 * @create: 2023 -01-03
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Invoke {

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
     * On success string.
     *
     * @return the string
     */
    String onSuccess() default StringUtils.EMPTY;


    /**
     * On fail string.
     *
     * @return the string
     */
    String onFail() default StringUtils.EMPTY;

    /**
     * Necessary string.
     *
     * @return the string
     */
    String necessary() default StringUtils.EMPTY;

    /**
     * Rollback string.
     *
     * @return the string
     */
    String rollback() default StringUtils.EMPTY;

}