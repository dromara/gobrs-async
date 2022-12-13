/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gobrs.async.spi;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The type Extension loader.
 *
 * @param <T> the type parameter
 */
@SuppressWarnings("all")
public final class ExtensionLoader<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ExtensionLoader.class);

    private static final String GOBRS_ASYNC_DIRECTORY = "META-INF/gobrs/";

    private static final Map<Class<?>, ExtensionLoader<?>> LOADERS = new ConcurrentHashMap<>();

    private final Class<T> clazz;

    private final ClassLoader classLoader;

    private final Holder<Map<String, ClassEntity>> cachedClasses = new Holder<>();

    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    private final Map<Class<?>, Object> realizeInstances = new ConcurrentHashMap<>();

    private String cachedDefaultName;

    private final Comparator<Holder<Object>> holderComparator = (o1, o2) -> {
        if (o1.getOrder() > o2.getOrder()) {
            return 1;
        } else if (o1.getOrder() < o2.getOrder()) {
            return -1;
        } else {
            return 0;
        }
    };

    private final Comparator<ClassEntity> classEntityComparator = (o1, o2) -> {
        if (o1.getOrder() > o2.getOrder()) {
            return 1;
        } else if (o1.getOrder() < o2.getOrder()) {
            return -1;
        } else {
            return 0;
        }
    };

    /**
     * Instantiates a new Extension loader.
     *
     * @param clazz the clazz.
     */
    private ExtensionLoader(final Class<T> clazz, final ClassLoader cl) {
        this.clazz = clazz;
        this.classLoader = cl;
        if (!Objects.equals(clazz, ExtensionFactory.class)) {
            ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getExtensionClassesEntity();
        }
    }

    /**
     * Gets extension loader.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @param cl    the cl
     * @return the extension loader.
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(final Class<T> clazz, final ClassLoader cl) {

        Objects.requireNonNull(clazz, "extension clazz is null");

        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("extension clazz (" + clazz + ") is not interface!");
        }
        if (!clazz.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException("extension clazz (" + clazz + ") without @" + SPI.class + " Annotation");
        }
        ExtensionLoader<T> extensionLoader = (ExtensionLoader<T>) LOADERS.get(clazz);
        if (Objects.nonNull(extensionLoader)) {
            return extensionLoader;
        }
        LOADERS.putIfAbsent(clazz, new ExtensionLoader<>(clazz, cl));
        return (ExtensionLoader<T>) LOADERS.get(clazz);
    }

    /**
     * Gets extension loader.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the extension loader
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(final Class<T> clazz) {
        return getExtensionLoader(clazz, ExtensionLoader.class.getClassLoader());
    }

    /**
     * Gets default realize.
     *
     * @return the default realize
     */
    public T getDefaultRealize() {
        getExtensionClassesEntity();
        if (StringUtils.isBlank(cachedDefaultName)) {
            return null;
        }
        return getRealize(cachedDefaultName);
    }

    /**
     * Gets realize.
     *
     * @param name the name
     * @return the realize.
     */
    public T getRealize(final String name) {
        if (StringUtils.isBlank(name)) {
            throw new NullPointerException("get realize name is null");
        }
        Holder<Object> objectHolder = cachedInstances.get(name);
        if (Objects.isNull(objectHolder)) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            objectHolder = cachedInstances.get(name);
        }
        Object value = objectHolder.getValue();
        if (Objects.isNull(value)) {
            synchronized (cachedInstances) {
                value = objectHolder.getValue();
                if (Objects.isNull(value)) {
                    Holder<T> pair = createExtension(name);
                    value = pair.getValue();
                    int order = pair.getOrder();
                    objectHolder.setValue(value);
                    objectHolder.setOrder(order);
                }
            }
        }
        return (T) value;
    }

    /**
     * get all realize spi.
     *
     * @return list. realize
     */
    public List<T> getRealizes() {
        Map<String, ClassEntity> extensionClassesEntity = this.getExtensionClassesEntity();
        if (extensionClassesEntity.isEmpty()) {
            return Collections.emptyList();
        }
        if (Objects.equals(extensionClassesEntity.size(), cachedInstances.size())) {
            return (List<T>) this.cachedInstances.values().stream()
                    .sorted(holderComparator)
                    .map(e -> {
                        return e.getValue();
                    }).collect(Collectors.toList());
        }
        List<T> realizes = new ArrayList<>();
        List<ClassEntity> classEntities = extensionClassesEntity.values().stream()
                .sorted(classEntityComparator).collect(Collectors.toList());
        classEntities.forEach(v -> {
            T realize = this.getRealize(v.getName());
            realizes.add(realize);
        });
        return realizes;
    }

    @SuppressWarnings("unchecked")
    private Holder<T> createExtension(final String name) {
        ClassEntity classEntity = getExtensionClassesEntity().get(name);
        if (Objects.isNull(classEntity)) {
            throw new IllegalArgumentException("name is error");
        }
        Class<?> aClass = classEntity.getClazz();
        Object o = realizeInstances.get(aClass);
        if (Objects.isNull(o)) {
            try {
                realizeInstances.putIfAbsent(aClass, aClass.newInstance());
                o = realizeInstances.get(aClass);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Extension instance(name: " + name + ", class: "
                        + aClass + ")  could not be instantiated: " + e.getMessage(), e);

            }
        }
        Holder<T> objectHolder = new Holder<>();
        objectHolder.setOrder(classEntity.getOrder());
        objectHolder.setValue((T) o);
        return objectHolder;
    }

    /**
     * Gets extension classes.
     *
     * @return the extension classes
     */
    public Map<String, Class<?>> getRealizesMaps() {
        Map<String, ClassEntity> classes = this.getExtensionClassesEntity();
        return classes.values().stream().collect(Collectors.toMap(e -> e.getName(), x -> x.getClazz(), (a, b) -> a));
    }

    private Map<String, ClassEntity> getExtensionClassesEntity() {
        Map<String, ClassEntity> classes = cachedClasses.getValue();
        if (Objects.isNull(classes)) {
            synchronized (cachedClasses) {
                classes = cachedClasses.getValue();
                if (Objects.isNull(classes)) {
                    classes = loadExtensionClass();
                    cachedClasses.setValue(classes);
                    cachedClasses.setOrder(0);
                }
            }
        }
        return classes;
    }

    private Map<String, ClassEntity> loadExtensionClass() {
        SPI annotation = clazz.getAnnotation(SPI.class);
        if (Objects.nonNull(annotation)) {
            String value = annotation.value();
            if (StringUtils.isNotBlank(value)) {
                cachedDefaultName = value;
            }
        }
        Map<String, ClassEntity> classes = new HashMap<>(16);
        loadDirectory(classes);
        return classes;
    }

    /**
     *
     */
    private void loadDirectory(final Map<String, ClassEntity> classes) {
        String fileName = GOBRS_ASYNC_DIRECTORY + clazz.getName();
        try {
            Enumeration<URL> urls = Objects.nonNull(this.classLoader) ? classLoader.getResources(fileName)
                    : ClassLoader.getSystemResources(fileName);
            if (Objects.nonNull(urls)) {
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    loadResources(classes, url);
                }
            }
        } catch (IOException t) {
            LOG.error("load extension class error {}", fileName, t);
        }
    }

    private void loadResources(final Map<String, ClassEntity> classes, final URL url) throws IOException {
        try (InputStream inputStream = url.openStream()) {
            Properties properties = new Properties();
            properties.load(inputStream);
            properties.forEach((k, v) -> {
                String name = (String) k;
                String classPath = (String) v;
                if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(classPath)) {
                    try {
                        loadClass(classes, name, classPath);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException("load extension resources error", e);
                    }
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException("load extension resources error", e);
        }
    }

    private void loadClass(final Map<String, ClassEntity> classes,
                           final String name, final String classPath) throws ClassNotFoundException {
        Class<?> subClass = Objects.nonNull(this.classLoader) ? Class.forName(classPath, true, this.classLoader) : Class.forName(classPath);
        if (!clazz.isAssignableFrom(subClass)) {
            throw new IllegalStateException("load extension resources error," + subClass + " subtype is not of " + clazz);
        }
        if (!subClass.isAnnotationPresent(Realize.class)) {
            throw new IllegalStateException("load extension resources error," + subClass + " without @" + Realize.class + " annotation");
        }
        ClassEntity oldClassEntity = classes.get(name);
        if (Objects.isNull(oldClassEntity)) {
            Realize realizeAnnotation = subClass.getAnnotation(Realize.class);
            ClassEntity classEntity = new ClassEntity(name, realizeAnnotation.order(), subClass);
            classes.put(name, classEntity);
        } else if (!Objects.equals(oldClassEntity.getClazz(), subClass)) {
            throw new IllegalStateException("load extension resources error,Duplicate class " + clazz.getName() + " name "
                    + name + " on " + oldClassEntity.getClazz().getName() + " or " + subClass.getName());
        }
    }

    /**
     * The type Holder.
     *
     * @param <T> the type parameter.
     */
    public static class Holder<T> {

        private volatile T value;

        private Integer order;

        /**
         * Gets value.
         *
         * @return the value
         */
        public T getValue() {
            return value;
        }

        /**
         * Sets value.
         *
         * @param value the value
         */
        public void setValue(final T value) {
            this.value = value;
        }

        /**
         * set order.
         *
         * @param order order.
         */
        public void setOrder(final Integer order) {
            this.order = order;
        }

        /**
         * get order.
         *
         * @return order. order
         */
        public Integer getOrder() {
            return order;
        }
    }

    /**
     * The type Class entity.
     */
    static final class ClassEntity {

        /**
         * name.
         */
        private String name;

        /**
         * order.
         */
        private Integer order;

        /**
         * class.
         */
        private Class<?> clazz;

        private ClassEntity(final String name, final Integer order, final Class<?> clazz) {
            this.name = name;
            this.order = order;
            this.clazz = clazz;
        }

        /**
         * get class.
         *
         * @return class. clazz
         */
        public Class<?> getClazz() {
            return clazz;
        }

        /**
         * set class.
         *
         * @param clazz class.
         */
        public void setClazz(final Class<?> clazz) {
            this.clazz = clazz;
        }

        /**
         * get name.
         *
         * @return name. name
         */
        public String getName() {
            return name;
        }

        /**
         * get order.
         *
         * @return order. order
         */
        public Integer getOrder() {
            return order;
        }
    }
}
