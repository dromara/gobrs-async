package com.gobrs.async.threadpool;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;


/**
 * The type Gobrs thread pool configuration.
 *
 * @program: gobrs -async
 * @ClassName GobrsThreadPoolConfiguration
 * @description:
 * @author: sizegang
 * @create: 2022 -09-28
 */
public abstract class GobrsThreadPoolConfiguration implements InitializingBean {

    @Autowired(required = false)
    private GobrsAsyncThreadPoolFactory gobrsAsyncThreadPoolFactory;

    /**
     * Initialize.
     *
     * @param factory the factory
     */
    public void initialize(GobrsAsyncThreadPoolFactory factory) {
        doInitialize(factory);
    }

    /**
     * Do initialize.
     *
     * @param factory the factory
     */
    protected abstract void doInitialize(GobrsAsyncThreadPoolFactory factory);

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Objects.nonNull(gobrsAsyncThreadPoolFactory)) {
            initialize(gobrsAsyncThreadPoolFactory);
        }
    }
}
