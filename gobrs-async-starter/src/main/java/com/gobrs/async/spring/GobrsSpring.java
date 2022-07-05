package com.gobrs.async.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The type Gobrs spring.
 *
 * @author sizegang1
 * @date 2022 -01-27 23:56 2022-01-27 23:56
 */
public class GobrsSpring implements ApplicationContextAware, BeanFactoryPostProcessor {

    /**
     * The constant applicationContext.
     */
    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (GobrsSpring.applicationContext == null) {
            GobrsSpring.applicationContext = applicationContext;
        }
    }

    /**
     * Gets application context.
     *
     * @return the application context
     */
//获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    /**
     * Gets bean.
     *
     * @param name the name
     * @return the bean
     */
//通过name获取 Bean.
    public static Object getBean(String name) {
        return getApplicationContext() == null ? cf.getBean(name) : getApplicationContext().getBean(name);
    }

    /**
     * Gets bean.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the bean
     */
//通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        ApplicationContext applicationContext = getApplicationContext();
        if (applicationContext == null) {
            return cf.getBean(clazz);
        }
        return applicationContext.getBean(clazz);
    }

    /**
     * Gets bean.
     *
     * @param <T>   the type parameter
     * @param name  the name
     * @param clazz the clazz
     * @return the bean
     */
//通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    private static ConfigurableListableBeanFactory cf;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        cf = beanFactory;
    }
}
