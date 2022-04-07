package com.gobrs.async.anno;

import com.gobrs.async.def.DefaultConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: gobrs-async-core
 * @description: Gobrs Async annotations
 * @author: sizegang
 * @date 2022-04-07 23:43
 * @author sizegang1
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Task {
    String name() default "gobrsAsyncName";

    boolean callback() default false;

    int retryCount() default DefaultConfig.retryCount;
}
