// package work.metanet.feng.common.security.component;

// import cn.hutool.core.util.StrUtil;
// import work.metanet.feng.common.core.constant.SecurityConstants;
// import work.metanet.feng.common.core.util.KeyStrResolver;
// import lombok.RequiredArgsConstructor;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.connection.RedisConnectionFactory;
// import org.springframework.security.oauth2.provider.OAuth2Authentication;
// import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
// import org.springframework.security.oauth2.provider.token.TokenStore;

// /**
//  * FengTokenStoreAutoConfiguration 类用于自动配置 Redis TokenStore，主要解决 OAuth2 认证中
//  * 令牌存储的配置问题。
//  * <p>
//  * 本类通过自动配置 Redis 为 TokenStore 提供存储支持，同时结合租户隔离机制，在生成
//  * 认证密钥时加入租户ID，确保不同租户之间的数据隔离。
//  * </p>
//  */
// @RequiredArgsConstructor
// @Configuration(proxyBeanMethods = false)
// public class FengTokenStoreAutoConfiguration {

//     private final KeyStrResolver resolver;  // 租户ID提取和解析工具
//     private final RedisConnectionFactory connectionFactory;  // Redis 连接工厂

//     /**
//      * 配置 TokenStore，使用 Redis 存储 OAuth2 令牌。
//      * <p>
//      * 本方法返回一个 FengRedisTokenStore 实例，并设置 TokenStore 的相关属性，包括
//      * 前缀和认证密钥生成器。认证密钥生成器在生成密钥时会根据租户ID进行隔离，确保多租户
//      * 环境下的数据隔离。
//      * </p>
//      *
//      * @return 配置好的 TokenStore 实例
//      */
//     @Bean
//     public TokenStore tokenStore() {
//         // 创建一个 FengRedisTokenStore 实例
//         FengRedisTokenStore tokenStore = new FengRedisTokenStore(connectionFactory, resolver);
        
//         // 设置 Redis 键的前缀，避免与其他应用的 Redis 键冲突
//         tokenStore.setPrefix(SecurityConstants.FENG_PREFIX + SecurityConstants.OAUTH_PREFIX);
        
//         // 设置认证密钥生成器，支持租户隔离
//         tokenStore.setAuthenticationKeyGenerator(new DefaultAuthenticationKeyGenerator() {
//             @Override
//             public String extractKey(OAuth2Authentication authentication) {
//                 // 提取原生认证密钥，并加入租户ID，形成租户隔离的认证密钥
//                 return resolver.extract(super.extractKey(authentication), StrUtil.COLON);
//             }
//         });
        
//         return tokenStore;
//     }

// }
