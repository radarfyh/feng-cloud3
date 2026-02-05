package work.metanet.feng.auth.config;

import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.common.security.component.FengDaoAuthenticationProvider;
import work.metanet.feng.common.security.grant.CustomAppAuthenticationProvider;
import work.metanet.feng.common.security.handler.FormAuthenticationFailureHandler;
import work.metanet.feng.common.security.handler.MobileLoginSuccessHandler;
import work.metanet.feng.common.security.handler.SsoLogoutSuccessHandler;
import work.metanet.feng.common.security.handler.TenantSavedRequestAwareAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * 认证相关配置类
 * <p>
 * 该类用于配置 Spring Security 的核心安全策略，包括 HTTP 安全策略、认证管理器、密码编码器等。
 * 通过继承 {@link WebSecurityConfigurerAdapter}，自定义安全配置行为。
 * </p>
 */
@Primary
@Order(90)
@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
	private static final String LOGIN_PAGE_PATH = "/token/login";
    private static final String LOGIN_PROCESSING_URL_PATH = "/token/form";
    
	@Override
//	@SneakyThrows
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin()
				.loginPage(LOGIN_PAGE_PATH)
				.loginProcessingUrl(LOGIN_PROCESSING_URL_PATH)
				.successHandler(tenantSavedRequestAwareAuthenticationSuccessHandler())
				.failureHandler(authenticationFailureHandler())
			.and().logout()
				.logoutSuccessHandler(logoutSuccessHandler())
				.deleteCookies("JSESSIONID")
				.invalidateHttpSession(true)
			.and().authorizeRequests()
				.antMatchers("/token/**", "/actuator/**", "/mobile/**", "/deviceNetworkCenter/authenticateByAppKey")
				.permitAll()
				.anyRequest()
				.authenticated()
			.and().csrf()
				.disable();
	}

    /**
     * 配置认证管理器
     * <p>
     * 该方法用于配置认证管理器，注入自定义的认证提供者（如 {@link FengDaoAuthenticationProvider} 和 {@link CustomAppAuthenticationProvider}）。
     * </p>
     *
     * @param auth 认证管理器构建器
     */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		FengDaoAuthenticationProvider daoAuthenticationProvider = new FengDaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

		// 处理默认的密码模式认证
		auth.authenticationProvider(daoAuthenticationProvider);
		// 自定义的认证模式
		auth.authenticationProvider(new CustomAppAuthenticationProvider());
	}

    /**
     * 配置忽略的静态资源
     * <p>
     * 该方法用于配置 Spring Security 忽略的静态资源路径。
     * </p>
     *
     * @param web Web 安全配置器
     */
	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/favicon.ico", "/css/**", "/error");
	}

    /**
     * 暴露认证管理器 Bean
     *
     * @return 认证管理器实例
     * @throws Exception 如果获取认证管理器时发生错误
     */
	@Bean
	@Override
//	@SneakyThrows
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

    /**
     * 配置认证失败处理器
     *
     * @return 认证失败处理器实例
     */
	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return new FormAuthenticationFailureHandler();
	}

    /**
     * 配置登出成功处理器
     *
     * @return 登出成功处理器实例
     */
	@Bean
	public LogoutSuccessHandler logoutSuccessHandler() {
		return new SsoLogoutSuccessHandler();
	}

    /**
     * 配置移动端登录成功处理器
     *
     * @return 移动端登录成功处理器实例
     */
	@Bean
	public AuthenticationSuccessHandler mobileLoginSuccessHandler() {
		return new MobileLoginSuccessHandler();
	}

    /**
     * 配置具备租户传递能力的登录成功处理器
     *
     * @return 具备租户传递能力的登录成功处理器实例
     */
	@Bean
	public AuthenticationSuccessHandler tenantSavedRequestAwareAuthenticationSuccessHandler() {
		return new TenantSavedRequestAwareAuthenticationSuccessHandler();
	}

	/**
	 * 配置密码编码器
	 * @return 动态密码编码器实例 密文
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder delegateingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		
		log.debug("passwordEncoder --> delegateingPasswordEncoder: {}", delegateingPasswordEncoder);

		return delegateingPasswordEncoder;
	}

}
