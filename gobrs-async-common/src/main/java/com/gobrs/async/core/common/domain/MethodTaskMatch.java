package com.gobrs.async.core.common.domain;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * The type Method task match.
 *
 * @program: gobrs -async
 * @ClassName MethodTaskMatch
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
@Builder
@Data
public class MethodTaskMatch {

    private Method method;

    private Object[] params;


}
