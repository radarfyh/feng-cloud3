package work.metanet.feng.auth.endpoint;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysOrgan;
import work.metanet.feng.admin.api.entity.SysTenant;
import work.metanet.feng.admin.api.feign.RemoteOrganService;
import work.metanet.feng.admin.api.feign.RemoteTenantService;
import work.metanet.feng.admin.api.vo.SysTenantVO;
import work.metanet.feng.auth.service.FengTokenDealServiceImpl;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.security.annotation.Inner;
import work.metanet.feng.common.security.service.FengUser;
import work.metanet.feng.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 令牌端点控制器
 * <p>
 * 该类用于处理与令牌相关的请求，包括登录页面、确认授权页面、退出登录、删除令牌、查询令牌等。
 * </p>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class FengTokenEndpoint {
    private static final String LOGIN_VIEW_NAME = "ftl/login";
    private static final String CONFIRM_VIEW_NAME = "ftl/confirm";

	private final ClientDetailsService clientDetailsService;

	private final FengTokenDealServiceImpl dealService;

	private final RemoteTenantService remoteTenantService;

    /**
     * 认证页面
     * <p>
     * 该方法用于显示登录页面，并返回租户列表。废弃
     * </p>
     *
     * @param modelAndView 模型和视图
     * @param error 表单登录失败处理回调的错误信息
     * @return 模型和视图
     */
	@GetMapping("/login")
	public ModelAndView require(ModelAndView modelAndView, @RequestParam(required = false) String error) {
		SysTenant tenant = new SysTenant();
		R<List<SysTenantVO>> sysTenantList = remoteTenantService.list(tenant, SecurityConstants.FROM_IN);
		log.debug("require --> error: {}, sysTenantList: {}", error, sysTenantList.toString());
		
		modelAndView.setViewName("LOGIN_VIEW_NAME");
		modelAndView.addObject("error", error);
		modelAndView.addObject("tenantList", sysTenantList.getData());
		return modelAndView;
	}

    /**
     * 确认授权页面
     * <p>
     * 该方法用于显示确认授权页面，展示授权范围和客户端信息。
     * </p>
     *
     * @param request HTTP 请求
     * @param session HTTP 会话
     * @param modelAndView 模型和视图
     * @return 模型和视图
     */
	@GetMapping("/confirm_access")
	public ModelAndView confirm(HttpServletRequest request, HttpSession session, ModelAndView modelAndView) {
		log.debug("confirm --> session:{}", session);

		Map<String, Object> scopeList = (Map<String, Object>) request.getAttribute("scopes");
		modelAndView.addObject("scopeList", scopeList.keySet());

		Object auth = session.getAttribute("authorizationRequest");
		if (auth != null) {
			AuthorizationRequest authorizationRequest = (AuthorizationRequest) auth;

			String clientId = authorizationRequest.getClientId();
			log.debug("confirm --> clientId:{}", clientId);
			
			ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
			FengUser user = SecurityUtils.getUser();
			
			modelAndView.addObject("app", clientDetails.getAdditionalInformation());
			modelAndView.addObject("user", SecurityUtils.getUser());
			
			log.debug("confirm --> clientDetails: {}, user: {}", clientDetails.toString(), user.toString());
		}

		modelAndView.setViewName(CONFIRM_VIEW_NAME);
		return modelAndView;
	}

    /**
     * 退出登录
     * <p>
     * 该方法用于处理退出登录请求，删除指定的令牌。
     * </p>
     *
     * @param authHeader Authorization 头
     * @return 操作结果
     */
	@DeleteMapping("/logout")
	public R<Boolean> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
		if (StrUtil.isBlank(authHeader)) {
			return R.ok(Boolean.FALSE, "退出失败，token 为空");
		}		
		
		String tokenValue = authHeader.replace(OAuth2AccessToken.BEARER_TYPE, StrUtil.EMPTY).trim();
		log.debug("logout --> tokenValue:{}", tokenValue);
		
		return delToken(tokenValue);
	}

    /**
     * 删除令牌
     * <p>
     * 该方法用于删除指定的令牌。
     * </p>
     *
     * @param token 令牌
     * @return 操作结果
     */
	@Inner
	@DeleteMapping("/{token}")
	public R<Boolean> delToken(@PathVariable("token") String token) {
		R<Boolean> resp = dealService.removeToken(token);
		log.debug("delToken --> token: {}, resp: {}", token, resp);
		return resp;
	}

    /**
     * 查询令牌列表
     * <p>
     * 该方法用于分页查询令牌列表，支持按用户名过滤。
     * </p>
     *
     * @param page 分页参数
     * @param username 用户名
     * @return 分页结果
     */
//	@SuppressWarnings("unchecked")
	@Inner
	@GetMapping("/page")
	public R<Page> tokenList(Page page, String username) {
		
		R<Page> resultPage;
		
		// 根据username 查询 token 列表
		if (StrUtil.isNotBlank(username)) {
			resultPage = dealService.queryTokenByUsername(page, username);			
		} else {
			resultPage = dealService.queryToken(page);
		}
		log.debug("tokenList --> username:{}, resultPage: {}", username, resultPage);
		
		return resultPage;
	}

    /**
     * 查询令牌信息
     * <p>
     * 该方法用于查询指定令牌的详细信息。
     * </p>
     *
     * @param token 令牌
     * @return 令牌信息
     */
	@Inner
	@GetMapping("/query-token")
	public R queryToken(String token) {
		R resp = dealService.queryTokenInfo(token);
		
		log.debug("queryToken --> token:{}, resp: ", token, resp);

		return resp;
	}
}
