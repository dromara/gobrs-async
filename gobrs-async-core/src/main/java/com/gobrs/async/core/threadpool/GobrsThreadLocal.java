package com.gobrs.async.core.threadpool;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * The type Gobrs thread local.
 *
 * @param <T> the type parameter
 * @program: gobrs -async
 * @ClassName GobrsThreadLocal
 * @description:
 * @author: sizegang
 * @create: 2022 -08-25
 */
public class GobrsThreadLocal<T> extends TransmittableThreadLocal<T> {
}
