package com.gobrs.async.core.holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The type Gobrs com.gobrs.async.spring.
 *
 * @author sizegang1
 * @date 2022 -01-27 23:56
 */
public class BeanHolder implements ApplicationContextAware, BeanFactoryPostProcessor {

    /**
     * The Logger.
     */
    static Logger logger = LoggerFactory.getLogger(BeanHolder.class);
    /**
     * The constant applicationContext.
     */
    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (BeanHolder.applicationContext == null) {
            BeanHolder.applicationContext = applicationContext;
        }
    }

    /**
     * Gets application context.
     * 获取applicationContext
     *
     * @return the application context
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    /**
     * Gets bean.
     *
     * @param name the name
     * @return the bean
     */
    public static Object getBean(String name) {
        try {
            return getApplicationContext() == null ? cf.getBean(name) : getApplicationContext().getBean(name);
        } catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
            if (getApplicationContext() == null) {
                return cf.getBean(lowerFirstChar(name));
            }
            return getApplicationContext().getBean(lowerFirstChar(name));
        } catch (Exception ex) {
            logger.error("Gobrs-Spring genBean error ", ex);
        }
        return null;
    }

    private static String lowerFirstChar(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * Gets bean.
     * 通过class获取Bean
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the bean
     */
    public static <T> T getBean(Class<T> clazz) {
        try {
            ApplicationContext applicationContext = getApplicationContext();
            if (applicationContext == null) {
                return cf.getBean(clazz);
            }
            return applicationContext.getBean(clazz);
        } catch (Exception exception) {
            logger.warn(" class {} getBean error{}", clazz, exception);
            return null;
        }
    }

    /**
     * Gets bean.
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param <T>   the type parameter
     * @param name  the name
     * @param clazz the clazz
     * @return the bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    private static ConfigurableListableBeanFactory cf;

    /**
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        cf = beanFactory;
    }
}
