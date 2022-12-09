package com.gobrs.async.core.anno;

import com.gobrs.async.core.common.def.DefaultConfig;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * The interface Task.
 *
 * @author sizegang1
 * @program: gobrs -async-core
 * @author: sizegang
 * @date 2022 -04-07
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Task {

    String value() default Strings.EMPTY;

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

    /**
     * Retry times
     *
     * @return int int
     */
    int retryCount() default DefaultConfig.RETRY_COUNT;

    int timeoutInMilliseconds() default DefaultConfig.TASK_TIME_OUT;

}
