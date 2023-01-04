package com.gobrs.async.core.anno;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Method task.
 *
 * @program: gobrs -async
 * @ClassName MethodTask
 * @description:
 * @author: sizegang
 * @create: 2023 -01-03
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface MethodTask {
    /**
     * Name string.
     *
     * @return the string
     */
    String name() default StringUtils.EMPTY;


    /**
     * Invoke invoke.
     *
     * @return the invoke
     */
    Invoke invoke() default @Invoke();


    /**
     * Invoke invoke.
     *
     * @return the invoke
     */
    MethodConfig config() default @MethodConfig();


    /**
     * Transaction com.gobrs.async.com.gobrs.async.test.task
     *
     * @return boolean boolean
     */
    boolean callback() default false;
}
