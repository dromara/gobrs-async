package com.gobrs.async.core.scan;

import com.gobrs.async.core.anno.Invoke;
import com.gobrs.async.core.anno.MethodComponent;
import com.gobrs.async.core.anno.MethodTask;
import com.gobrs.async.core.cache.GCache;
import com.gobrs.async.core.cache.GCacheManager;
import com.gobrs.async.core.common.domain.MethodTaskMatch;
import com.gobrs.async.core.common.enums.GCacheEnum;
import com.gobrs.async.core.common.exception.DuplicateMethodTaskException;
import com.gobrs.async.core.property.GobrsAsyncProperties;
import com.gobrs.async.core.task.MethodTaskAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
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
@Slf4j
public class MethodComponentScanner extends BaseScannner implements ApplicationContextAware {

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
        Map<String, Object> values = applicationContext.getBeansWithAnnotation(MethodComponent.class);
        GCache<String, MethodTaskAdapter, Map<String, MethodTaskAdapter>> methodTaskCache = gCacheManager.getGCache(GCacheEnum.METHOD_TASK.getType());
        Map<String, MethodTaskAdapter> instance = methodTaskCache.instance();
        values.forEach((k, v) -> {
            Method[] methods = v.getClass().getMethods();
            for (Method method : methods) {
                MethodTask methodTask = AnnotationUtils.getAnnotation(method, MethodTask.class);
                if (Objects.nonNull(methodTask)) {
                    analysis(k, v, method, methodTask, instance);
                }
            }
        });
    }

    private void analysis(String k, Object v, Method method, MethodTask methodTask, Map<String, MethodTaskAdapter> instance) {
        MethodTaskAdapter methodTaskAdaptation = applicationContext.getBean(MethodTaskAdapter.class);
        methodTaskAdaptation.setName(methodTask.name());
        methodTaskAdaptation.setMethodTask(methodTask);
        methodTaskAdaptation.setProxy(v);

        Invoke invoke = methodTask.invoke();

        Map<String, MethodTaskMatch> PARAMETERS_CACHE = new ConcurrentHashMap<>();

        MethodTaskMatch match = MethodTaskMatch.builder().method(method).parameters(method.getParameters()).build();
        PARAMETERS_CACHE.put(invoke.task(), match);
        methodTaskAdaptation.setPARAMETERS_CACHE(PARAMETERS_CACHE);

        MethodTaskAdapter existed = instance.get(method.getName());
        if (Objects.nonNull(existed)) {
            throw new DuplicateMethodTaskException(method.getName());
        }
        instance.put(method.getName(), methodTaskAdaptation);
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

    /**
     * The type Method task factory bean.
     */
    public static class MethodTaskFactoryBean implements FactoryBean<MethodTaskAdapter> {

        @Override
        public MethodTaskAdapter getObject() throws Exception {
            return new MethodTaskAdapter();
        }

        @Override
        public Class<?> getObjectType() {
            return MethodTaskAdapter.class;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }

}
