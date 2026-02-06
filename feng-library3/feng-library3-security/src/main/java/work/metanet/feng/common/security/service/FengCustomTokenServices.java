// package work.metanet.feng.common.security.service;

// import cn.hutool.core.map.MapUtil;
// import work.metanet.feng.common.core.constant.CommonConstants;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.InitializingBean;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.oauth2.common.*;
// import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
// import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
// import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
// import org.springframework.security.oauth2.provider.*;
// import org.springframework.security.oauth2.provider.token.*;
// import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.util.Assert;

// import java.util.*;

// /**
//  * 自定义的 OAuth2 令牌服务实现类，用于管理访问令牌和刷新令牌。
//  * <p>
//  * 本类负责创建、存储和检索 OAuth2 访问令牌和刷新令牌。它提供了生成新访问令牌、刷新过期令牌、 
//  * 并根据配置的限制管理用户的并发会话的功能。还确保客户端凭证的正确验证，并确保令牌在需要时正确过期或撤销。
//  * </p>
//  * <p>
//  * 本服务支持访问令牌的创建和刷新令牌的管理。它能够管理每个用户的多个令牌，并根据客户端的配置处理令牌的过期和续期。
//  * </p>
//  * <p>
//  * 它还支持使用令牌存储，并提供在令牌过期或客户端超出允许的活动会话数时撤销令牌的功能。
//  * </p>
//  */
// @Slf4j
// public class FengCustomTokenServices implements AuthorizationServerTokenServices, ResourceServerTokenServices,
// 		ConsumerTokenServices, InitializingBean {

// 	private int refreshTokenValiditySeconds = 60 * 60 * 24 * 30; // default 30 days.

// 	private int accessTokenValiditySeconds = 60 * 60 * 24; // default 24 hours.

// 	private int defaultOnlineQuantity = 0; // 客户端默认同时在线数量

// 	private boolean supportRefreshToken = false;

// 	private boolean reuseRefreshToken = true;

// 	private TokenStore tokenStore;

// 	private ClientDetailsService clientDetailsService;

// 	private TokenEnhancer accessTokenEnhancer;

// 	private AuthenticationManager authenticationManager;

// 	/**
// 	 * 初始化 token 服务，确保所需的依赖已正确设置。
// 	 * <p>
// 	 * 该方法在属性设置后调用，确保 tokenStore 已被正确注入。如果没有为 tokenStore 设置值，
// 	 * 则会抛出异常，确保在应用启动时所有必要的依赖都已配置完毕。
// 	 * </p>
// 	 */
// 	public void afterPropertiesSet() {
// 	    // 校验 tokenStore 是否被正确设置
// 	    Assert.notNull(tokenStore, "tokenStore must be set. Please ensure that the tokenStore bean is properly configured.");
	    
// 	    // 在此处可以加入其他初始化逻辑，例如日志记录等，以便于调试和监控
// 	    log.debug("Token services initialized successfully with tokenStore: {}", tokenStore);
// 	}

// 	/**
// 	 * 创建访问令牌。如果已经存在有效的令牌，则返回该令牌。否则，创建新的访问令牌。
// 	 * 如果当前令牌已过期或超出了允许的在线数量，则会删除过期或最旧的令牌。
// 	 *
// 	 * @param authentication 包含用户认证信息的 OAuth2Authentication 对象
// 	 * @return 创建的 OAuth2AccessToken 对象
// 	 * @throws AuthenticationException 如果认证过程中出现问题
// 	 */
// 	@Override
// 	public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
// 	    // 获取当前用户的已有访问令牌
// 	    OAuth2AccessToken existingAccessToken = tokenStore.getAccessToken(authentication);
// 	    OAuth2RefreshToken refreshToken = null;
//     	// 如果sys_oauth_client_details附加信息中没有配置在线数量，那么默认为defaultOnlineQuantity（0）
//         int onlineQuantity = getOnlineQuantity(authentication.getOAuth2Request());
// 	    // 如果已有访问令牌
// 	    if (existingAccessToken != null) {
// 	        if (existingAccessToken.isExpired()) {
//                 // 如果令牌过期，移除相关的刷新令牌和访问令牌
//                 handleExpiredToken(existingAccessToken);
//             } else {
//                 // 如果令牌未过期，更新令牌信息并返回
//                 tokenStore.storeAccessToken(existingAccessToken, authentication);
//                 return existingAccessToken;
//             }
// 	    }
// 	    // 如果 online 数量有限制，执行检查
// 	    if (onlineQuantity > 0) {
// 	        handleOnlineQuantityLimit(authentication, onlineQuantity);
// 	    }
// 	    // 创建新的刷新令牌和访问令牌
// 	    refreshToken = createRefreshToken(authentication);
// 	    OAuth2AccessToken accessToken = createAccessToken(authentication, refreshToken);

// 	    // 存储新的访问令牌
// 	    tokenStore.storeAccessToken(accessToken, authentication);

// 	    // 如果新的访问令牌有刷新令牌，则也存储刷新令牌
// 	    refreshToken = accessToken.getRefreshToken();
// 	    if (refreshToken != null) {
// 	        tokenStore.storeRefreshToken(refreshToken, authentication);
// 	    }

// 	    // 返回创建的新访问令牌
// 	    return accessToken;
// 	}
// 	/**
// 	 * 处理已过期的访问令牌，删除相关的刷新令牌和访问令牌
// 	 */
// 	private void handleExpiredToken(OAuth2AccessToken existingAccessToken) {
// 	    if (existingAccessToken.getRefreshToken() != null) {
// 	        OAuth2RefreshToken refreshToken = existingAccessToken.getRefreshToken();
// 	        tokenStore.removeRefreshToken(refreshToken);
// 	    }
// 	    tokenStore.removeAccessToken(existingAccessToken);
// 	    log.info("Expired token removed from store.");
// 	}

// 	/**
// 	 * 处理并删除超过同时在线数量限制的令牌
// 	 */
// 	private void handleOnlineQuantityLimit(OAuth2Authentication authentication, int onlineQuantity) {
// 	    // 获取当前客户端和用户名下的所有令牌
// 	    Collection<OAuth2AccessToken> currentTokenList = tokenStore.findTokensByClientIdAndUserName(
// 	            authentication.getOAuth2Request().getClientId(), authentication.getName());
	    
// 	    // 如果已达到在线数量上限，则删除最旧的令牌
// 	    if (currentTokenList.size() >= onlineQuantity) {
// 	        Optional<OAuth2AccessToken> oldestTokenOpt = currentTokenList.stream()
// 	                .min(Comparator.comparing(OAuth2AccessToken::getExpiration));
// 	        oldestTokenOpt.ifPresent(oldestToken -> {
// 	            log.info("Online quantity limit reached, removing the oldest token.");
// 	            tokenStore.removeAccessToken(oldestToken);
// 	            if (oldestToken.getRefreshToken() != null) {
// 	                OAuth2RefreshToken refreshToken = oldestToken.getRefreshToken();
// 	                tokenStore.removeRefreshToken(refreshToken);
// 	            }
// 	        });
// 	    }
// 	}
	
// 	/**
// 	 * 刷新访问令牌。通过提供的刷新令牌生成新的访问令牌，并验证请求是否合法。
// 	 * 如果刷新令牌有效且未过期，则生成新的访问令牌。如果刷新令牌过期或无效，则抛出异常。
// 	 * 
// 	 * @param refreshTokenValue 刷新令牌的值
// 	 * @param tokenRequest 用于生成新令牌的请求信息
// 	 * @return 刷新的访问令牌
// 	 * @throws AuthenticationException 如果刷新令牌无效或认证失败，则抛出异常
// 	 */
// 	@Transactional(noRollbackFor = { InvalidTokenException.class, InvalidGrantException.class })
// 	public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest)
// 	        throws AuthenticationException {

// 	    // 检查是否支持刷新令牌
// 	    if (!supportRefreshToken) {
// 	        throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
// 	    }

// 	    // 从存储中读取刷新令牌
// 	    OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(refreshTokenValue);
// 	    if (refreshToken == null) {
// 	        throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
// 	    }

// 	    // 获取与刷新令牌关联的 OAuth2Authentication 对象
// 	    OAuth2Authentication authentication = tokenStore.readAuthenticationForRefreshToken(refreshToken);

// 	    // 如果提供了 authenticationManager，则尝试重新认证用户
// 	    if (this.authenticationManager != null && !authentication.isClientOnly()) {
// 	        Authentication user = new PreAuthenticatedAuthenticationToken(authentication.getUserAuthentication(), "",
// 	                authentication.getAuthorities());
// 	        user = authenticationManager.authenticate(user);
// 	        Object details = authentication.getDetails();
// 	        authentication = new OAuth2Authentication(authentication.getOAuth2Request(), user);
// 	        authentication.setDetails(details);
// 	    }

// 	    // 校验 clientId 是否匹配
// 	    String clientId = authentication.getOAuth2Request().getClientId();
// 	    if (clientId == null || !clientId.equals(tokenRequest.getClientId())) {
// 	        throw new InvalidGrantException("Wrong client for this refresh token: " + refreshTokenValue);
// 	    }

// 	    // 清除与刷新令牌关联的现有访问令牌
// 	    tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);

// 	    // 如果刷新令牌已过期，则移除刷新令牌并抛出异常
// 	    if (isExpired(refreshToken)) {
// 	        tokenStore.removeRefreshToken(refreshToken);
// 	        throw new InvalidTokenException("Invalid refresh token (expired): " + refreshToken);
// 	    }

// 	    // 使用提供的 tokenRequest 生成更新后的认证信息
// 	    authentication = createRefreshedAuthentication(authentication, tokenRequest);

// 	    // 根据配置决定是否重用刷新令牌
// 	    if (!reuseRefreshToken) {
// 	        tokenStore.removeRefreshToken(refreshToken);
// 	        refreshToken = createRefreshToken(authentication);
// 	    }

// 	    // 创建新的访问令牌并存储
// 	    OAuth2AccessToken accessToken = createAccessToken(authentication, refreshToken);
// 	    tokenStore.storeAccessToken(accessToken, authentication);

// 	    // 如果不重用刷新令牌，存储新的刷新令牌
// 	    if (!reuseRefreshToken) {
// 	        tokenStore.storeRefreshToken(accessToken.getRefreshToken(), authentication);
// 	    }

// 	    // 返回新的访问令牌
// 	    return accessToken;
// 	}

// 	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
// 		return tokenStore.getAccessToken(authentication);
// 	}

// 	/**
// 	 * 创建一个更新后的认证信息，基于请求的范围来更新客户端认证。
// 	 * 如果请求的范围超出了原始范围，则抛出异常。
// 	 * 
// 	 * @param authentication 原始的认证信息
// 	 * @param request 请求信息，其中包含了要刷新令牌的范围
// 	 * @return 更新后的认证信息
// 	 * @throws InvalidScopeException 如果请求的范围无效或比原始范围更宽
// 	 */
// 	private OAuth2Authentication createRefreshedAuthentication(OAuth2Authentication authentication, TokenRequest request) {
// 	    // 通过客户端请求来刷新 OAuth2Request
// 	    OAuth2Request clientAuth = authentication.getOAuth2Request().refresh(request);
	    
// 	    // 获取请求中传递的范围（scope）
// 	    Set<String> scope = request.getScope();
	    
// 	    // 如果传递了新的范围（scope），则检查它是否符合原始的范围
// 	    if (scope != null && !scope.isEmpty()) {
// 	        Set<String> originalScope = clientAuth.getScope();
	        
// 	        // 校验新的范围是否在原始范围之内
// 	        if (originalScope == null || !originalScope.containsAll(scope)) {
// 	            throw new InvalidScopeException(
// 	                    "Unable to narrow the scope of the client authentication to " + scope + ".", originalScope);
// 	        } else {
// 	            // 如果范围有效，则更新客户端认证范围
// 	            clientAuth = clientAuth.narrowScope(scope);
// 	        }
// 	    }
	    
// 	    // 创建并返回新的 OAuth2Authentication 对象，包含更新后的 OAuth2Request 和用户认证信息
// 	    return new OAuth2Authentication(clientAuth, authentication.getUserAuthentication());
// 	}

// 	/**
// 	 * 判断刷新令牌是否已过期。
// 	 * 
// 	 * @param refreshToken 刷新令牌对象
// 	 * @return 如果令牌已过期，返回 true；否则返回 false
// 	 */
// 	protected boolean isExpired(OAuth2RefreshToken refreshToken) {
// 	    // 检查刷新令牌是否为过期的刷新令牌类型
// 	    if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
// 	        ExpiringOAuth2RefreshToken expiringToken = (ExpiringOAuth2RefreshToken) refreshToken;
	        
// 	        // 如果令牌的过期时间为空或当前时间超过了过期时间，则认为令牌已过期
// 	        return expiringToken.getExpiration() == null
// 	                || System.currentTimeMillis() > expiringToken.getExpiration().getTime();
// 	    }
// 	    return false;
// 	}

// 	public OAuth2AccessToken readAccessToken(String accessToken) {
// 		return tokenStore.readAccessToken(accessToken);
// 	}

// 	/**
// 	 * 根据访问令牌值加载认证信息。
// 	 * 该方法首先验证访问令牌是否有效，如果有效则返回 OAuth2Authentication 对象。
// 	 * 如果令牌过期或无效，则抛出 InvalidTokenException。
// 	 * 
// 	 * @param accessTokenValue 访问令牌的值
// 	 * @return OAuth2Authentication 认证信息
// 	 * @throws AuthenticationException 如果认证过程中发生错误
// 	 * @throws InvalidTokenException 如果令牌无效或过期
// 	 */
// 	public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException, InvalidTokenException {
// 	    // 从 tokenStore 中读取 access token
// 	    OAuth2AccessToken accessToken = tokenStore.readAccessToken(accessTokenValue);

// 	    // 检查 access token 是否为 null，若是则抛出 InvalidTokenException
// 	    if (accessToken == null) {
// 	        throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
// 	    }

// 	    // 检查 access token 是否已过期，若是则移除 token 并抛出 InvalidTokenException
// 	    if (accessToken.isExpired()) {
// 	        tokenStore.removeAccessToken(accessToken);
// 	        throw new InvalidTokenException("Access token expired: " + accessTokenValue);
// 	    }

// 	    // 根据 access token 获取 OAuth2Authentication 对象
// 	    OAuth2Authentication result = tokenStore.readAuthentication(accessToken);
	    
// 	    // 如果无法从 tokenStore 中找到对应的 authentication，则抛出 InvalidTokenException
// 	    if (result == null) {
// 	        throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
// 	    }

// 	    // 如果 clientDetailsService 被配置，则检查 clientId 是否有效
// 	    if (clientDetailsService != null) {
// 	        String clientId = result.getOAuth2Request().getClientId();
	        
// 	        // 日志记录，用于调试
// 	        log.debug("loadAuthentication --> accessTokenValue: {}, accessToken: {}, result: {}, clientId: {}", accessTokenValue, accessToken, result, clientId);

// 	        try {
// 	            // 验证 clientId 是否有效
// 	            clientDetailsService.loadClientByClientId(clientId);
// 	        } catch (ClientRegistrationException e) {
// 	            throw new InvalidTokenException("Client not valid: " + clientId, e);
// 	        }
// 	    }

// 	    return result;
// 	}

// 	/**
// 	 * 获取客户端 ID。
// 	 * 该方法根据访问令牌值加载认证信息，并返回关联的客户端 ID。
// 	 * 如果访问令牌无效或未关联客户端，则抛出 InvalidTokenException。
// 	 * 
// 	 * @param tokenValue 访问令牌的值
// 	 * @return 客户端 ID
// 	 * @throws InvalidTokenException 如果令牌无效或未关联客户端
// 	 */
// 	public String getClientId(String tokenValue) {
// 	    // 从 tokenStore 中读取 OAuth2Authentication 信息
// 	    OAuth2Authentication authentication = tokenStore.readAuthentication(tokenValue);
	    
// 	    // 如果无法读取到认证信息，则抛出 InvalidTokenException
// 	    if (authentication == null) {
// 	        throw new InvalidTokenException("Invalid access token: " + tokenValue);
// 	    }

// 	    // 获取 OAuth2Request 对象中的 clientId
// 	    OAuth2Request clientAuth = authentication.getOAuth2Request();
	    
// 	    // 如果 OAuth2Request 中没有 clientId，则抛出 InvalidTokenException
// 	    if (clientAuth == null) {
// 	        throw new InvalidTokenException("Invalid access token (no client id): " + tokenValue);
// 	    }

// 	    // 返回 clientId
// 	    return clientAuth.getClientId();
// 	}

// 	/**
// 	 * 撤销访问令牌和对应的刷新令牌（如果存在）。
// 	 * 该方法根据传入的访问令牌值从存储中查找并删除访问令牌。如果访问令牌存在且包含刷新令牌，
// 	 * 还会删除相应的刷新令牌。成功撤销令牌后返回 true，否则返回 false。
// 	 *
// 	 * @param tokenValue 访问令牌的值
// 	 * @return 如果成功撤销令牌，返回 true；否则返回 false
// 	 */
// 	public boolean revokeToken(String tokenValue) {
// 	    // 从 tokenStore 中读取访问令牌
// 	    OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);

// 	    // 如果访问令牌不存在，返回 false
// 	    if (accessToken == null) {
// 	        log.warn("Failed to revoke token: {}. Token not found.", tokenValue);
// 	        return false;
// 	    }

// 	    // 如果访问令牌存在并且有刷新令牌，删除刷新令牌
// 	    OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
// 	    if (refreshToken != null) {
// 	        tokenStore.removeRefreshToken(refreshToken);
// 	        log.info("Revoked refresh token for access token: {}", tokenValue);
// 	    }

// 	    // 删除访问令牌
// 	    tokenStore.removeAccessToken(accessToken);
// 	    log.info("Revoked access token: {}", tokenValue);

// 	    // 返回成功撤销令牌
// 	    return true;
// 	}

// 	/**
// 	 * 创建一个刷新令牌（refresh token）。
// 	 * 该方法首先检查是否支持刷新令牌，如果支持，则创建一个新的刷新令牌。刷新令牌的有效期
// 	 * 是基于配置的有效时间（如果存在）来决定的。如果没有有效期，则生成一个永不过期的刷新令牌。
// 	 *
// 	 * @param authentication OAuth2Authentication 对象，包含有关认证的信息。
// 	 * @return OAuth2RefreshToken 刷新令牌，如果不支持刷新令牌则返回 null。
// 	 */
// 	private OAuth2RefreshToken createRefreshToken(OAuth2Authentication authentication) {
// 	    // 检查是否支持刷新令牌
// 	    if (!isSupportRefreshToken(authentication.getOAuth2Request())) {
// 	        return null;
// 	    }

// 	    // 获取刷新令牌的有效期（以秒为单位）
// 	    int validitySeconds = getRefreshTokenValiditySeconds(authentication.getOAuth2Request());

// 	    // 生成新的刷新令牌的值
// 	    String value = UUID.randomUUID().toString();

// 	    // 如果有效期大于0，则创建一个带有过期时间的刷新令牌
// 	    if (validitySeconds > 0) {
// 	        // 创建过期的刷新令牌
// 	        return new DefaultExpiringOAuth2RefreshToken(value,
// 	                new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
// 	    }

// 	    // 否则，创建一个永不过期的刷新令牌
// 	    return new DefaultOAuth2RefreshToken(value);
// 	}

// 	/**
// 	 * 创建一个访问令牌（access token）。
// 	 * 该方法根据传入的 OAuth2Authentication 和刷新令牌创建一个新的访问令牌，访问令牌的有效期
// 	 * 是基于配置的有效时间来决定的。如果有效期大于0，则设置过期时间；否则，访问令牌永不过期。
// 	 *
// 	 * @param authentication OAuth2Authentication 对象，包含有关认证的信息。
// 	 * @param refreshToken OAuth2RefreshToken 对象，关联的刷新令牌。
// 	 * @return OAuth2AccessToken 访问令牌，可能会经过增强处理。
// 	 */
// 	private OAuth2AccessToken createAccessToken(OAuth2Authentication authentication, OAuth2RefreshToken refreshToken) {
// 	    // 创建新的默认访问令牌
// 	    DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());

// 	    // 获取访问令牌的有效期（以秒为单位）
// 	    int validitySeconds = getAccessTokenValiditySeconds(authentication.getOAuth2Request());

// 	    // 如果有效期大于0，则设置过期时间
// 	    if (validitySeconds > 0) {
// 	        // 设置访问令牌的过期时间
// 	        token.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
// 	    }

// 	    // 设置刷新令牌
// 	    token.setRefreshToken(refreshToken);

// 	    // 设置访问令牌的作用域
// 	    token.setScope(authentication.getOAuth2Request().getScope());

// 	    // 如果存在自定义的令牌增强器，则使用它来增强访问令牌
// 	    return accessTokenEnhancer != null ? accessTokenEnhancer.enhance(token, authentication) : token;
// 	}


// 	/**
// 	 * The access token validity period in seconds
// 	 * @param clientAuth the current authorization request
// 	 * @return the access token validity period in seconds
// 	 */
// 	protected int getAccessTokenValiditySeconds(OAuth2Request clientAuth) {
// 		if (clientDetailsService != null) {
// 			ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
// 			Integer validity = client.getAccessTokenValiditySeconds();
// 			if (validity != null && validity > 0) {
// 				return validity;
// 			}
// 		}
// 		return accessTokenValiditySeconds;
// 	}

// 	/**
// 	 * The refresh token validity period in seconds
// 	 * @param clientAuth the current authorization request
// 	 * @return the refresh token validity period in seconds
// 	 */
// 	protected int getRefreshTokenValiditySeconds(OAuth2Request clientAuth) {
// 		if (clientDetailsService != null) {
// 			ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
// 			Integer validity = client.getRefreshTokenValiditySeconds();
// 			if (validity != null && validity > 0) {
// 				return validity;
// 			}
// 		}
// 		return refreshTokenValiditySeconds;
// 	}

// 	/**
// 	 * the client online quantity
// 	 * @param clientAuth the current authorization request
// 	 * @return the client online quantity
// 	 */
// 	protected int getOnlineQuantity(OAuth2Request clientAuth) {
// 		if (clientDetailsService != null) {
// 			ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
// 			return MapUtil.getInt(client.getAdditionalInformation(), CommonConstants.ONLINE_QUANTITY, defaultOnlineQuantity);
// 		}
// 		return defaultOnlineQuantity;
// 	}

// 	/**
// 	 * Is a refresh token supported for this client (or the global setting if
// 	 * {@link #setClientDetailsService(ClientDetailsService) clientDetailsService} is not
// 	 * set.
// 	 * @param clientAuth the current authorization request
// 	 * @return boolean to indicate if refresh token is supported
// 	 */
// 	protected boolean isSupportRefreshToken(OAuth2Request clientAuth) {
// 		if (clientDetailsService != null) {
// 			ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
// 			return client.getAuthorizedGrantTypes().contains("refresh_token");
// 		}
// 		return this.supportRefreshToken;
// 	}

// 	/**
// 	 * An access token enhancer that will be applied to a new token before it is saved in
// 	 * the token store.
// 	 * @param accessTokenEnhancer the access token enhancer to set
// 	 */
// 	public void setTokenEnhancer(TokenEnhancer accessTokenEnhancer) {
// 		this.accessTokenEnhancer = accessTokenEnhancer;
// 	}

// 	/**
// 	 * The validity (in seconds) of the refresh token. If less than or equal to zero then
// 	 * the tokens will be non-expiring.
// 	 * @param refreshTokenValiditySeconds The validity (in seconds) of the refresh token.
// 	 */
// 	public void setRefreshTokenValiditySeconds(int refreshTokenValiditySeconds) {
// 		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
// 	}

// 	/**
// 	 * The default validity (in seconds) of the access token. Zero or negative for
// 	 * non-expiring tokens. If a client details service is set the validity period will be
// 	 * read from the client, defaulting to this value if not defined by the client.
// 	 * @param accessTokenValiditySeconds The validity (in seconds) of the access token.
// 	 */
// 	public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
// 		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
// 	}

// 	/**
// 	 * Whether to support the refresh token.
// 	 * @param supportRefreshToken Whether to support the refresh token.
// 	 */
// 	public void setSupportRefreshToken(boolean supportRefreshToken) {
// 		this.supportRefreshToken = supportRefreshToken;
// 	}

// 	/**
// 	 * Whether to reuse refresh tokens (until expired).
// 	 * @param reuseRefreshToken Whether to reuse refresh tokens (until expired).
// 	 */
// 	public void setReuseRefreshToken(boolean reuseRefreshToken) {
// 		this.reuseRefreshToken = reuseRefreshToken;
// 	}

// 	/**
// 	 * The persistence strategy for token storage.
// 	 * @param tokenStore the store for access and refresh tokens.
// 	 */
// 	public void setTokenStore(TokenStore tokenStore) {
// 		this.tokenStore = tokenStore;
// 	}

// 	/**
// 	 * An authentication manager that will be used (if provided) to check the user
// 	 * authentication when a token is refreshed.
// 	 * @param authenticationManager the authenticationManager to set
// 	 */
// 	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
// 		this.authenticationManager = authenticationManager;
// 	}

// 	/**
// 	 * The client details service to use for looking up clients (if necessary). Optional
// 	 * if the access token expiry is set globally via
// 	 * {@link #setAccessTokenValiditySeconds(int)}.
// 	 * @param clientDetailsService the client details service
// 	 */
// 	public void setClientDetailsService(ClientDetailsService clientDetailsService) {
// 		this.clientDetailsService = clientDetailsService;
// 	}

// }
