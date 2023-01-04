package com.gobrs.async.core.anno;

import com.gobrs.async.core.common.def.DefaultConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

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
     * Task string.
     *
     * @return the string
     */
    String task() default StringUtils.EMPTY;

    /**
     * Rollback string.
     *
     * @return the string
     */
    String rollback() default StringUtils.EMPTY;


}