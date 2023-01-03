package com.gobrs.async.core.common.domain;

import com.gobrs.async.core.common.def.DefaultConfig;
import lombok.Builder;
import lombok.Data;

/**
 * The type Any condition.
 *
 * @param <T> the type parameter T is the return result
 * @program: gobrs -async
 * @ClassName AnyCondition
 * @description:
 * @author: sizegang
 * @create: 2022 -09-29
 */
@Data
@Builder
public class AnyConditionResult<T> {

    private Boolean state;

    private T result;



}
