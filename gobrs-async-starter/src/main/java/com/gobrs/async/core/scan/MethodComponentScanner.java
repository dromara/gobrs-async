package com.gobrs.async.core.scan;

import com.gobrs.async.core.anno.EnabledMethodTask;
import com.gobrs.async.core.anno.MethodTask;
import com.gobrs.async.core.cache.GCacheManager;
import com.gobrs.async.core.property.GobrsAsyncProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The type Method component scanner.
 *
 * @program: gobrs -async
 * @ClassName MethodComponentScanner
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
public class MethodComponentScanner extends BaseScannner implements ImportBeanDefinitionRegistrar, ApplicationContextAware {

    private GCacheManager gCacheManager;

    private GobrsAsyncProperties gobrsAsyncProperties;

    private AtomicBoolean cousor = new AtomicBoolean(false);

    private ApplicationContext applicationContext;

    /**
     * Instantiates a new Method component scanner.
     *
     * @param gCacheManager        the g cache manager
     * @param gobrsAsyncProperties the gobrs async properties
     */
    public MethodComponentScanner(GCacheManager gCacheManager, GobrsAsyncProperties gobrsAsyncProperties) {
        this.gCacheManager = gCacheManager;
        this.gobrsAsyncProperties = gobrsAsyncProperties;
    }

    @Override
    public void doScan() {
        Map<String, Object> values = applicationContext.getBeansWithAnnotation(EnabledMethodTask.class);

        values.forEach((k, v) -> {
            Method[] methods = v.getClass().getMethods();
            for (Method method : methods) {
                MethodTask methodTask = AnnotationUtils.getAnnotation(method, MethodTask.class);
                if (Objects.nonNull(methodTask)) {

                }
            }
        });


    }

    @Override
    public void init() {
        if (cousor.compareAndSet(false, true)) {
            doScan();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);
    }
}
