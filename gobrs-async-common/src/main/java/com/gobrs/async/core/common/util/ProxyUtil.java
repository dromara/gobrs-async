package com.gobrs.async.core.common.util;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * The type Proxy util.
 *
 * @program: gobrs -async
 * @ClassName ProxyUtil
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
public class ProxyUtil {


    /**
     * Invoke method object.
     *
     * @param method     the method
     * @param target     the target
     * @param parameters the parameters
     * @return the object
     */
    public static Object invokeMethod(Method method, Object target, Parameter[] parameters) {
        return ReflectionUtils.invokeMethod(method, target, parameters);
    }
}
