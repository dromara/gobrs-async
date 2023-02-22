package com.gobrs.async.core.anno;

import com.gobrs.async.core.autoconfig.GobrsMethodTaskConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Enabled method task.
 *
 * @program: gobrs -async
 * @ClassName EnabledMethodTask
 * @description:
 * @author: sizegang
 * @create: 2023 -01-03
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(GobrsMethodTaskConfiguration.class)
public @interface EnabledMethodTask {

}
