package com.gobrs.async.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: gobrs-async-core
 * @description: Gobrs Async annotations
 * @author: sizegang
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Task {
    String name() default "gobrsAsyncName";
}
