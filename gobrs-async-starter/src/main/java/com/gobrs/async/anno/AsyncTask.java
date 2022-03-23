package com.gobrs.async.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: gobrs-async-core
 * @description: Gobrs Async annotations
 * @author: sizegang
 * @create: 2022-03-23 23:21
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncTask {

    String key() default "gobrsAsyncKey";

    String name() default "gobrsAsyncName";

}
