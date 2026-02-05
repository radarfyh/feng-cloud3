package work.metanet.feng.common.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Spring上下文工具类，用于获取Spring容器中bean，并提供一些常用操作。
 * <p>
 * 该类主要用于在没有依赖注入的情况下获取Spring容器中的Bean。
 * </p>
 */
@Slf4j
@Service
@Lazy(false) // 如果不需要懒加载，可以移除该注解
public class SpringContextHolder implements BeanFactoryPostProcessor, ApplicationContextAware, DisposableBean {

    /**
     * Spring的BeanFactory容器，用于存储所有的Bean定义。
     */
    private static ConfigurableListableBeanFactory beanFactory;

    /**
     * Spring的ApplicationContext实例，用于获取容器中的Bean。
     */
    private static ApplicationContext applicationContext = null;

    /**
     * 获取存储在静态变量中的ApplicationContext实例。
     *
     * @return ApplicationContext实例
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * BeanFactoryPostProcessor实现，注入ApplicationContext到静态变量中。
     *
     * @param factory Spring容器的BeanFactory
     * @throws BeansException 异常
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        SpringContextHolder.beanFactory = factory;
    }

    /**
     * 实现ApplicationContextAware接口，注入ApplicationContext到静态变量中。
     *
     * @param applicationContext Spring上下文
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
        if (log.isDebugEnabled()) {
            log.debug("已设置SpringContextHolder中的ApplicationContext：" + applicationContext);
        }
    }

    /**
     * 获取BeanFactory。如果beanFactory为null，使用ApplicationContext作为备选。
     *
     * @return ListableBeanFactory实例
     */
    public static ListableBeanFactory getBeanFactory() {
        return (null == beanFactory) ? applicationContext : beanFactory;
    }

    /**
     * 根据名称获取Bean实例。
     *
     * @param name Bean的名称
     * @param <T>  Bean的类型
     * @return Bean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) getBeanFactory().getBean(name);
    }

    /**
     * 根据类型获取Bean实例。
     *
     * @param type Bean的类型
     * @param <T>  Bean的类型
     * @return Bean实例
     */
    public static <T> T getBean(Class<T> requiredType) {
        return getBeanFactory().getBean(requiredType);
    }

    /**
     * 获取指定类型的所有Bean实例。
     *
     * @param type Bean的类型
     * @param <T>  Bean的类型
     * @return 类型为T的所有Bean实例的Map
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return getBeanFactory().getBeansOfType(type);
    }

    /**
     * 清除SpringContextHolder中的ApplicationContext，避免内存泄漏。
     */
    public static void clearHolder() {
        if (log.isDebugEnabled()) {
            log.debug("清除SpringContextHolder中的ApplicationContext：" + applicationContext);
        }
        applicationContext = null;
        beanFactory = null; // 清空beanFactory，防止内存泄漏
    }

    /**
     * 发布事件到Spring上下文。
     *
     * @param event 需要发布的事件
     */
    public static void publishEvent(ApplicationEvent event) {
        // 如果 applicationContext 为 null，则表明 SpringContextHolder 尚未初始化
        if (applicationContext == null) {
            // 更详细的日志，帮助诊断问题
            log.error("Spring上下文为空，无法发布事件。请确保 SpringContextHolder 已经正确初始化。");
            throw new IllegalStateException("Spring上下文为空，无法发布事件");
        }

        applicationContext.publishEvent(event);
    }

    /**
     * 实现DisposableBean接口，在Spring容器销毁时清理静态变量。
     */
    @Override
    public void destroy() {
        SpringContextHolder.clearHolder();
    }
    
    public void registerBean(String beanName, Object beanInstance) {
        BeanDefinitionRegistry beanDefinitionRegistry =
                (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition((Class<Object>) beanInstance.getClass(), () -> beanInstance);

        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();

        beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
    }

    public void unregisterBean(String beanName) {
        BeanDefinitionRegistry beanDefinitionRegistry =
                (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();

        if (beanDefinitionRegistry.containsBeanDefinition(beanName)) {
            beanDefinitionRegistry.removeBeanDefinition(beanName);
        }
    }
}
