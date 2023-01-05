package com.gobrs.async.core.threadpool;

import com.gobrs.async.core.holder.BeanHolder;
import com.gobrs.async.plugin.base.threadpool.ThreadPoolCreator;
import com.gobrs.async.spi.ExtensionLoader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;


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

    private static final AtomicBoolean sw = new AtomicBoolean(false);

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
        if (!sw.compareAndSet(false, true)) {
            return;
        }
        if (Objects.nonNull(gobrsAsyncThreadPoolFactory)) {
            ThreadPoolExecutor extensionThreadPool = getExtensionThreadPool();
            initialize(gobrsAsyncThreadPoolFactory);
            // Plug-ins have a higher priority
            if (Objects.nonNull(extensionThreadPool)) {
                gobrsAsyncThreadPoolFactory.setDefaultThreadPoolExecutor(extensionThreadPool);
            }
        }
    }

    private ThreadPoolExecutor getExtensionThreadPool() {
        ThreadPoolCreator creator = BeanHolder.getBean(ThreadPoolCreator.class);
        if (Objects.isNull(creator)) {
            return null;
        }
        return creator.create();
    }
}
