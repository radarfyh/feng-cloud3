package work.metanet.feng.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.common.security.component.FengCommenceAuthExceptionEntryPoint;
import work.metanet.feng.common.security.component.FengWebResponseExceptionTranslator;
import work.metanet.feng.common.security.grant.ResourceOwnerCustomeAppTokenGranter;
import work.metanet.feng.common.security.service.FengCustomTokenServices;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 认证服务器配置类
 * <p>
 * 该类用于配置 OAuth2 认证服务器的核心组件，包括客户端详情服务、令牌端点安全配置、令牌端点配置等。
 * 通过继承 {@link AuthorizationServerConfigurerAdapter}，自定义认证服务器的行为。
 * </p>
 */
@Configuration
@AllArgsConstructor
@EnableAuthorizationServer
@Slf4j
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    private static final String OAUTH_CONFIRM_ACCESS_PATH = "/oauth/confirm_access";
    private static final String TOKEN_CONFIRM_ACCESS_PATH = "/token/confirm_access";
    
//	private final ClientDetailsService fengClientDetailsServiceImpl;
    private final ClientDetailsService clientDetailsService;

	private final AuthenticationManager authenticationManagerBean;

	private final AuthorizationCodeServices authorizationCodeServices;

	private final AuthenticationManager authenticationManager;

	private final TokenStore redisTokenStore;

	private final TokenEnhancer tokenEnhancer;

	private final ObjectMapper objectMapper;

    /**
     * 配置客户端详情服务
     * <p>
     * 该方法用于配置客户端详情服务，指定客户端信息的加载方式。
     * 通过 {@link ClientDetailsServiceConfigurer} 配置客户端详情服务实现类。
     * </p>
     *
     * @param clients 客户端详情服务配置器
     * @throws Exception 如果配置过程中发生错误
     */
	@Override
	@SneakyThrows
	public void configure(ClientDetailsServiceConfigurer clients) {
		log.debug("configure --> clients: {}", clients);
		clients.withClientDetails(clientDetailsService);
	}

    /**
     * 配置认证服务器的安全策略
     * <p>
     * 该方法用于配置认证服务器的安全策略，包括允许表单认证、自定义认证入口点、令牌检查访问权限等。
     * </p>
     *
     * @param oauthServer 认证服务器安全配置器
     */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
		log.debug("configure --> oauthServer: {}", oauthServer);
		oauthServer.allowFormAuthenticationForClients()
				.authenticationEntryPoint(new FengCommenceAuthExceptionEntryPoint(objectMapper))
				.checkTokenAccess("isAuthenticated()");
	}

    /**
     * 配置认证服务器的端点
     * <p>
     * 该方法用于配置认证服务器的端点行为，包括支持的 HTTP 方法、令牌服务、令牌存储、令牌增强器、授权码服务、认证管理器等。
     * 同时，注入自定义的令牌授予者和异常翻译器。
     * </p>
     *
     * @param endpoints 认证服务器端点配置器
     */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		log.debug("configure --> endpoints: {}", endpoints);
		endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
				.tokenServices(tokenServices())
				.tokenStore(redisTokenStore)
				.tokenEnhancer(tokenEnhancer)
				.authorizationCodeServices(authorizationCodeServices)
				.authenticationManager(authenticationManagerBean)
				.reuseRefreshTokens(false)
				.pathMapping(OAUTH_CONFIRM_ACCESS_PATH, TOKEN_CONFIRM_ACCESS_PATH)
				.exceptionTranslator(new FengWebResponseExceptionTranslator());

		// 注入自定义认证类型
		setTokenGranter(endpoints);
	}

    /**
     * 设置自定义的令牌授予者
     * <p>
     * 该方法用于将自定义的令牌授予者（如 {@link ResourceOwnerCustomeAppTokenGranter}）添加到默认的令牌授予者列表中。
     * 通过 {@link CompositeTokenGranter} 组合多个令牌授予者。
     * </p>
     *
     * @param endpoints 认证服务器端点配置器
     */
	private void setTokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
		log.debug("setTokenGranter --> endpoints: {}", endpoints);
		// 获取默认授权类型
		TokenGranter tokenGranter = endpoints.getTokenGranter();
		
		ArrayList<TokenGranter> tokenGranters = new ArrayList<>(Arrays.asList(tokenGranter));
		
		ResourceOwnerCustomeAppTokenGranter resourceOwnerCustomeAppTokenGranter	
			= new ResourceOwnerCustomeAppTokenGranter(authenticationManager
					, endpoints.getTokenServices()
					, endpoints.getClientDetailsService()
					, endpoints.getOAuth2RequestFactory());
		tokenGranters.add(resourceOwnerCustomeAppTokenGranter);
		
		CompositeTokenGranter compositeTokenGranter = new CompositeTokenGranter(tokenGranters);
		endpoints.tokenGranter(compositeTokenGranter);
	}

    /**
     * 配置自定义的令牌服务
     * <p>
     * 该方法用于创建并配置自定义的令牌服务 {@link FengCustomTokenServices}，包括令牌存储、刷新令牌支持、客户端详情服务和令牌增强器。
     * </p>
     *
     * @return 自定义的令牌服务实例
     */
	@Bean
	public FengCustomTokenServices tokenServices() {
		FengCustomTokenServices tokenServices = new FengCustomTokenServices();
		tokenServices.setTokenStore(redisTokenStore);
		tokenServices.setSupportRefreshToken(true);
		tokenServices.setReuseRefreshToken(false);
		tokenServices.setClientDetailsService(clientDetailsService);
		tokenServices.setTokenEnhancer(tokenEnhancer);

		log.debug("tokenServices --> redisTokenStore: {}, tokenEnhancer: {}", redisTokenStore, tokenEnhancer);

		return tokenServices;
	}
}
