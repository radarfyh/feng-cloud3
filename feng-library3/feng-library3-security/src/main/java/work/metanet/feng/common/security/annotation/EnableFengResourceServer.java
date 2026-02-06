// package work.metanet.feng.common.security.annotation;

// import work.metanet.feng.common.security.component.FengResourceServerAutoConfiguration;
// import work.metanet.feng.common.security.component.FengSecurityBeanDefinitionRegistrar;
// import org.springframework.context.annotation.Import;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

// import java.lang.annotation.*;

// /**
//  * 启用 Feng 资源服务器配置注解
//  * <p>
//  * 此注解用于启用 Feng 资源服务器相关的配置，包括 OAuth2 资源服务器的支持。
//  * 通过此注解，Spring Security 会自动配置 OAuth2 资源服务器相关的功能。
//  * 还会启用方法级安全控制，允许在方法级别使用 @PreAuthorize 或 @Secured 注解。
//  * </p>
//  * <p>
//  * 使用该注解后，框架会自动启用以下功能：
//  * 1. 启用 Spring Security 的资源服务器功能（通过自定义配置类）。
//  * 2. 启用方法安全控制，支持使用 @PreAuthorize、@Secured 等注解进行方法权限控制。
//  * 3. 引入 Feng 资源服务器的自定义自动配置类和 Bean 定义注册类。
//  * </p>
//  */
// @Documented
// @Inherited
// @Target({ ElementType.TYPE })
// @Retention(RetentionPolicy.RUNTIME)
// @EnableMethodSecurity(prePostEnabled = true)  // 启用方法级安全控制，支持 @PreAuthorize 和 @Secured
// @Import({ FengResourceServerAutoConfiguration.class, FengSecurityBeanDefinitionRegistrar.class })
// public @interface EnableFengResourceServer {
//     // 此注解无需额外字段，因为所有的功能通过导入相应的配置和注解来实现
// }