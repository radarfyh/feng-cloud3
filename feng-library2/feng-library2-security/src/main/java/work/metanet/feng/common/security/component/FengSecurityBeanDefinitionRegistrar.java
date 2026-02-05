package work.metanet.feng.common.security.component;

import work.metanet.feng.common.core.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * FengSecurityBeanDefinitionRegistrar 是一个自定义的 Spring Bean 注册器，用于动态注册资源服务器相关的配置类。
 * <p>
 * 该类实现了 {@link ImportBeanDefinitionRegistrar} 接口，根据传入的注解信息注册资源服务器的配置类。
 * 它会根据注解值检查是否存在相应的资源服务器配置，如果存在则不进行重复注册。
 * </p>
 */
@Slf4j
public class FengSecurityBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     * 根据注解值动态注入资源服务器的相关配置属性。
     * <p>
     * 如果注册表中已存在资源服务器的配置类，则不进行重新注册，并输出警告信息。
     * 如果不存在配置类，则创建并注册 {@link FengLocalResourceServerConfigurerAdapter} 作为资源服务器配置。
     * </p>
     * 
     * @param metadata 注解信息，包含了配置类的元数据。
     * @param registry BeanDefinitionRegistry，Spring 注册器，用于注册 Bean 定义。
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        // 检查资源服务器配置是否已存在
        if (registry.isBeanNameInUse(SecurityConstants.RESOURCE_SERVER_CONFIGURER)) {
            // 如果已存在，输出警告信息并返回，避免重复注册
            log.warn("本地存在资源服务器配置，覆盖默认配置: " + SecurityConstants.RESOURCE_SERVER_CONFIGURER);
            return;
        }

        // 创建并注册资源服务器配置的 Bean 定义
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(FengLocalResourceServerConfigurerAdapter.class);
        registry.registerBeanDefinition(SecurityConstants.RESOURCE_SERVER_CONFIGURER, beanDefinition);

        log.info("成功注册资源服务器配置: {}", SecurityConstants.RESOURCE_SERVER_CONFIGURER);
    }

}
