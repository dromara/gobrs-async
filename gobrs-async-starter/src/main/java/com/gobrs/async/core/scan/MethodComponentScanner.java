package com.gobrs.async.core.scan;

import com.gobrs.async.core.anno.Invoke;
import com.gobrs.async.core.anno.MethodComponent;
import com.gobrs.async.core.anno.MethodConfig;
import com.gobrs.async.core.anno.MethodTask;
import com.gobrs.async.core.cache.GCache;
import com.gobrs.async.core.cache.GCacheManager;
import com.gobrs.async.core.common.domain.GobrsTaskMethodEnum;
import com.gobrs.async.core.common.domain.MethodTaskMatch;
import com.gobrs.async.core.common.enums.TaskEnum;
import com.gobrs.async.core.common.exception.DuplicateMethodTaskException;
import com.gobrs.async.core.property.GobrsAsyncProperties;
import com.gobrs.async.core.task.MethodTaskAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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

    private final AtomicBoolean cousor = new AtomicBoolean(false);

    private ApplicationContext applicationContext;

    private static final List<String> filterMethods = Arrays.asList("equals", "hashCode", "toString", "annotationType");

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
        GCache<String, MethodTaskAdapter, Map<String, MethodTaskAdapter>> methodTaskCache = gCacheManager.getGCache(TaskEnum.METHOD.getType());
        Map<String, MethodTaskAdapter> instance = methodTaskCache.instance();

        values.forEach((k, v) -> {
            Method[] methods = v.getClass().getMethods();
            for (Method targetMethod : methods) {
                MethodTask methodTask = AnnotationUtils.getAnnotation(targetMethod, MethodTask.class);
                if (Objects.nonNull(methodTask)) {
                    analysis(k, v, targetMethod, methodTask, instance);
                }
            }
        });
    }

    private void analysis(String k, Object v, Method targetMethod, MethodTask methodTask, Map<String, MethodTaskAdapter> instance) {

        MethodTaskAdapter methodTaskAdaptation = applicationContext.getBean(MethodTaskAdapter.class);

        methodTaskAdaptation.setName(methodTask.name());

        methodTaskAdaptation.setMethodTask(methodTask);

        methodTaskAdaptation.setProxy(v);

        String customizeName = methodTask.name();

        if (StringUtils.isBlank(customizeName)) {
            customizeName = targetMethod.getName();
        }

        Invoke invoke = methodTask.invoke();

        MethodConfig config = methodTask.config();

        Map<String, MethodTaskMatch> PARAMETERS_CACHE = new HashMap<>();

        extracted(invoke, PARAMETERS_CACHE, targetMethod);

        setter(methodTaskAdaptation, config, PARAMETERS_CACHE, customizeName);

        MethodTaskAdapter existed = instance.get(customizeName);

        if (Objects.nonNull(existed)) {
            throw new DuplicateMethodTaskException(customizeName);
        }

        instance.put(customizeName, methodTaskAdaptation);
    }


    private void setter(MethodTaskAdapter methodTaskAdaptation, MethodConfig config, Map<String, MethodTaskMatch> PARAMETERS_CACHE, String customizeName) {

        methodTaskAdaptation.setPARAMETERS_CACHE(PARAMETERS_CACHE);

        methodTaskAdaptation.setType(TaskEnum.METHOD.getType());

        methodTaskAdaptation.setRetryCount(config.retryCount());

        methodTaskAdaptation.setCallback(config.callback());

        methodTaskAdaptation.setTimeoutInMilliseconds(config.timeoutInMilliseconds());

        methodTaskAdaptation.setFailSubExec(config.failSubExec());

        methodTaskAdaptation.setExclusive(config.failSubExec());

        methodTaskAdaptation.setDesc(config.desc());

        methodTaskAdaptation.setName(customizeName);
    }


    private void extracted(Invoke invoke, Map<String, MethodTaskMatch> PARAMETERS_CACHE, Method targetMethod) {

        Method[] annoMethods = invoke.getClass().getDeclaredMethods();

        List<Method> collectMethods = Arrays.stream(annoMethods).filter(x -> !filterMethods.contains(x.getName())).collect(Collectors.toList());

        for (Method annoMethod : collectMethods) {

            Object value = ReflectionUtils.invokeMethod(annoMethod, invoke);

            if (GobrsTaskMethodEnum.TASK.getMethod().equals(annoMethod.getName())) {
                MethodTaskMatch match = MethodTaskMatch.builder().method(targetMethod).params(targetMethod.getParameters()).build();
                PARAMETERS_CACHE.put(annoMethod.getName(), match);
                continue;
            }

            if (value != null && StringUtils.isNotBlank(value.toString()) && !filterMethods.equals(annoMethod.getName()) && value instanceof String) {
                MethodTaskMatch match = MethodTaskMatch.builder().method(targetMethod).params(targetMethod.getParameters()).build();
                PARAMETERS_CACHE.put(annoMethod.getName(), match);
            }
        }
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
