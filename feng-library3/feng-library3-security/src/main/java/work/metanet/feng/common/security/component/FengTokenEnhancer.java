// package work.metanet.feng.common.security.component;

// import lombok.extern.slf4j.Slf4j;
// import work.metanet.feng.common.core.constant.CacheConstants;
// import work.metanet.feng.common.core.constant.SecurityConstants;
// import work.metanet.feng.common.core.util.SpringContextHolder;
// import work.metanet.feng.common.security.service.FengUser;

// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
// import org.springframework.security.oauth2.common.OAuth2AccessToken;
// import org.springframework.security.oauth2.provider.OAuth2Authentication;
// import org.springframework.security.oauth2.provider.token.TokenEnhancer;

// import java.time.Duration;
// import java.util.HashMap;
// import java.util.Map;

// /**
//  * FengTokenEnhancer 用于增强 OAuth2 访问令牌的功能。
//  * <p>
//  * 该类实现了 TokenEnhancer 接口，提供了一种定制化访问令牌的方式，
//  * 在生成新令牌时向令牌中添加额外的信息，比如客户端信息和用户信息。
//  * 如果是客户端模式（Client Credentials），则不增强用户信息。
//  * </p>
//  */
// @Slf4j
// public class FengTokenEnhancer implements TokenEnhancer {

//     /**
//      * 增强 OAuth2 访问令牌，添加额外的用户或客户端信息。
//      * <p>
//      * 该方法在生成访问令牌时将用户信息或客户端信息添加到令牌中。
//      * 如果是客户端模式，则只添加客户端信息；如果是用户模式，则添加用户的详细信息。
//      * </p>
//      * 
//      * @param accessToken 当前的 OAuth2 访问令牌，包括过期时间和刷新令牌
//      * @param authentication 当前的认证信息，包括客户端和用户的详细信息
//      * @return 增强后的 OAuth2 访问令牌
//      */
//     @Override
//     public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
//         log.debug("正在增强 accessToken: {}", accessToken);

//         // 初始化额外信息的存储
//         final Map<String, Object> additionalInfo = new HashMap<>(12);

//         // 获取客户端 ID
//         String clientId = authentication.getOAuth2Request().getClientId();
//         additionalInfo.put(SecurityConstants.CLIENT_ID, clientId);

//         // 客户端模式（Client Credentials），只返回客户端信息，不返回用户信息
//         if (SecurityConstants.CLIENT_CREDENTIALS.equals(authentication.getOAuth2Request().getGrantType())) {
//             log.debug("客户端模式，不返回用户信息");
//             additionalInfo.put(SecurityConstants.ACTIVE, Boolean.TRUE);
//             ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
//             return accessToken;
//         }

//         // 用户模式，添加用户信息到令牌
//         FengUser fengUser = (FengUser) authentication.getUserAuthentication().getPrincipal();
        
//         // 确保用户信息不为 null
//         if (fengUser != null) {
//             additionalInfo.put(SecurityConstants.DETAILS_USER_ID, fengUser.getId());
//             additionalInfo.put(SecurityConstants.DETAILS_USERNAME, fengUser.getUsername());
//             additionalInfo.put(SecurityConstants.DETAILS_PHONE, fengUser.getPhone());
//             additionalInfo.put(SecurityConstants.DETAILS_AVATAR, fengUser.getAvatar());
//             additionalInfo.put(SecurityConstants.DETAILS_DEPT_ID, fengUser.getDeptId());
//             additionalInfo.put(SecurityConstants.DETAILS_DEPT_CODE, fengUser.getDeptCode());
//             additionalInfo.put(SecurityConstants.DETAILS_FIRST_LOGIN, fengUser.getFirstLogin());
//             additionalInfo.put(SecurityConstants.DETAILS_TENANT_ID, fengUser.getTenantId());
//             additionalInfo.put(SecurityConstants.DETAILS_ORG_ID, fengUser.getOrgCode());
//         } else {
//             log.warn("未能从认证信息中获取用户信息，无法增强令牌");
//         }

//         // 设置增强后的信息
//         ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        
//         return accessToken;
//     }
// }
