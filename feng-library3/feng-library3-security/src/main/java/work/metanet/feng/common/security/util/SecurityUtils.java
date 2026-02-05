package work.metanet.feng.common.security.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import work.metanet.feng.admin.api.dto.UserInfo;
import work.metanet.feng.admin.api.feign.RemoteUserService;
import work.metanet.feng.common.core.config.TenantOfHeader;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.util.JasyptUtil;
import work.metanet.feng.common.core.util.SpringContextHolder;
import work.metanet.feng.common.security.component.FengRedisTokenStore;
import work.metanet.feng.common.security.service.FengUser;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * 安全工具类，提供与用户认证、角色获取相关的实用方法。
 * <p>
 * 本类提供了以下功能：
 * <ul>
 *   <li>获取当前用户的认证信息</li>
 *   <li>获取当前用户的信息（包括ID、用户名、手机号等）</li>
 *   <li>获取当前用户的角色信息</li>
 * </ul>
 * </p>
 */
@Slf4j
@UtilityClass
public class SecurityUtils {

    /**
     * 获取当前的 Authentication 对象。
     * <p>
     * 该方法通过 SecurityContext 获取当前用户的认证信息，返回当前的 Authentication 对象，
     * 该对象包含了认证的用户信息、权限等。
     * </p>
     * @return 当前的 Authentication 对象
     */
    public Authentication getAuthentication() {
    	if (BeanUtil.isEmpty(SecurityContextHolder.getContext())) {
    		log.debug("getAuthentication--》SecurityContextHolder.getContext()为空");
    		return null;
    	}
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	if (BeanUtil.isEmpty(auth)) {
    		log.debug("getAuthentication--》Authentication对象获取为空");
    	}
        return auth;
    }

    /**
     * 从 Authentication 中获取当前用户。
     * @param authentication 当前的 Authentication 对象
     * @return FengUser 用户对象，如果当前用户不是 FengUser 类型则返回 null
     */
    public FengUser getUser(Authentication authentication) {
    	if (authentication == null) {
    		log.debug("getUser-->authentication为空");
    		return null;
    	}
        Object principal = authentication.getPrincipal();
        if (principal instanceof FengUser) {
            return (FengUser) principal;
        }
        return null;
    }

    /**
     * 获取当前用户对象。
     * <p>
     * 此方法通过调用 getAuthentication() 获取当前的 Authentication 对象，再从中获取 FengUser。
     * </p>
     * @return FengUser 当前用户对象
     */
    public FengUser getUser() {
        Authentication authentication = getAuthentication();
        return getUser(authentication);
    }

    /**
     * 获取当前用户的角色集合。
     * <p>
     * 该方法会从当前用户的 Authentication 对象中提取出用户的所有权限，并过滤出角色 ID。
     * </p>
     * @return 当前用户的角色 ID 集合
     */
    public List<Integer> getRoles() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
        	log.debug("getRoles-->authentication为空");
        	return null;
        }
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<Integer> roleIds = new ArrayList<>(authorities.size());  // 预先分配空间
        authorities.stream()
                .filter(granted -> StrUtil.startWith(granted.getAuthority(), SecurityConstants.ROLE))  // 筛选角色权限
                .forEach(granted -> {
                    String id = StrUtil.removePrefix(granted.getAuthority(), SecurityConstants.ROLE);
                    try {
                        if(!id.equals("ANONYMOUS")) {
                        	roleIds.add(Integer.parseInt(id));  // 转换为角色 ID
                        }
                    } catch (NumberFormatException e) {
                        log.warn("Invalid role ID format: {}", id);  // 捕获无效角色 ID 格式
                    }
                });
        return roleIds;
    }
    
    /**
     * 解析租户信息 先读取参数，后读取头部
     * @param request
     * @return TenantOfHeader
     */
    public TenantOfHeader resolveTenantId(HttpServletRequest request) {
    	if (request == null) {
    		log.debug("resolveTenantId-->request为空");
    		return null;
    	}
    	
        // 优先级: Parameter > Header > 默认租户

    	// 1. 从参数中获取
    	String tenantId = request.getParameter(CommonConstants.TENANT_ID);
    	if (StrUtil.isNotBlank(tenantId)) {
    		TenantOfHeader tenant = new TenantOfHeader();
        	tenant.setTenantId(tenantId);
        	tenant.setUrl(request.getRequestURI());
        	return tenant;
        }
        
        // 2. 从头部获取
        String strEncryptTenant = request.getHeader(CommonConstants.TENANT_HEADER_KEY);
        log.debug("resolveTenantId-->接收到前端请求, 内容： {}, url: {}", strEncryptTenant, request.getRequestURI());
        
        // 3. 从默认设置中获取（暂时取消默认值，直接返回空）
        if (strEncryptTenant == null) {
        	log.debug("resolveTenantId-->strEncryptTenant为空");
        	return null;
        }

        // 解密头部中获取的值
        String strDecryptTenant = strEncryptTenant;
		try {
			strDecryptTenant = JasyptUtil.decryptAES(strEncryptTenant, CommonConstants.DEFAULT_TENANT_CRYPT_KEY);
		} catch (Exception e) {
			log.error("resolveTenantId-->报错：{}，原始串：{}，url：{}", e.getMessage(), strEncryptTenant, request.getRequestURI());
		}
		log.debug("resolveTenantId-->解密前: {}，解密后: {}, url:{}", strEncryptTenant, strDecryptTenant, request.getRequestURI());
        TenantOfHeader objTenant = JSONUtil.toBean(strDecryptTenant, TenantOfHeader.class);
        
        objTenant.setUrl(request.getRequestURI());
        
        return objTenant;
    }

    
    public String resolveTenantIdSimple(HttpServletRequest request) {
    	if (request == null) {
    		log.debug("resolveTenantIdSimple-->request为空");
    		return null;
    	}
        // 优先级: Parameter > Header > 默认租户

    	// 1. 从参数中获取
    	String tenantId = request.getParameter(CommonConstants.TENANT_ID);
    	if (StrUtil.isNotBlank(tenantId)) {
    		log.debug("resolveTenantIdSimple-->从HTTP参数获取租户ID： {}, url: {}", tenantId, request.getRequestURI());
        	return tenantId;
        }
        
        // 2. 从头部获取
    	tenantId = request.getHeader(CommonConstants.TENANT_HEADER_KEY);
    	if (StrUtil.isNotBlank(tenantId)) {
    		log.debug("resolveTenantIdSimple-->从HTTP头获取租户ID： {}, url: {}", tenantId, request.getRequestURI());
            Integer id = -1;
            try {
            	id = Integer.valueOf(tenantId);
            	return id.toString();
            } catch (Exception e) {
            	
            }
    	}
    	
    	// 3.从Redis获取
    	UserInfo info = resolveUserFromRedisToken(request);
    	if (info != null && info.getSysUser() != null ) {
	    	Integer id = info.getSysUser().getTenantId();
	    	
	    	if (id > 0 ) {
	    		tenantId = String.valueOf(id);
	    		log.debug("resolveTenantIdSimple-->从redis获取租户ID： {}, url: {}", tenantId, request.getRequestURI());
	    		return tenantId;
	    	}
    	}
    	
    	return null;
    }
    
    
    public static UserInfo resolveUserFromRedisToken(HttpServletRequest request) {
    	if (request == null) {
    		log.debug("resolveUserFromRedisToken-->request为空");
    		return null;
    	}
    	
        try {
            String authHeader = request.getHeader("Authorization");
            if (StrUtil.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
                log.debug("请求未携带合法的 Authorization 头");
                return null;
            }

            String token = authHeader.substring(7);
            RedisTemplate<String, Object> redisTemplate = SpringContextHolder.getBean("redisTemplate");

            // 枚举 Redis 中所有可能的 accessToken Key（注意此处不推荐 keys，建议 scan）
            Set<String> keys = redisTemplate.keys(CacheConstants.FENG_TENANT+":" +CacheConstants.USER_DETAILS+":*");
            if (keys != null) {
                for (String key : keys) {
                    Object storedToken = redisTemplate.opsForValue().get(key);
                    if (token.equals(storedToken)) {
                        // 从 token key 中提取 userId，再从用户服务获取用户详情
                        String username = key.substring(key.lastIndexOf(":") + 1);
                        
                        // 调用用户详情接口（或 Redis 缓存）获取 tenantId
                        RemoteUserService userService = SpringContextHolder.getBean(RemoteUserService.class);
                        UserInfo user = userService.info(username,SecurityConstants.FROM_IN).getData();
                        return user;
                    }
                }
            }
        } catch (Exception e) {
            log.error("resolveUserFromRedisToken 出错：", e);
        }
        return null;
    }
    
    /**
     * 从当前认证信息中获取 OAuth2AccessToken
     * <p>
     * 该方法先通过 Spring 上下文获取 FengRedisTokenStore Bean，
     * 再传入当前的 OAuth2Authentication，返回对应的访问令牌。
     * 如果当前未认证或不是 OAuth2Authentication，则返回 null。
     * </p>
     * @return OAuth2AccessToken 或 null
     */
    public OAuth2AccessToken getAccessToken() {
        Authentication authentication = getAuthentication();
        if (!(authentication instanceof OAuth2Authentication)) {
            log.debug("当前 Authentication 不是 OAuth2Authentication，无法获取访问令牌");
            return null;
        }
        OAuth2Authentication oauth2Auth = (OAuth2Authentication) authentication;
        // 从 Spring 容器中获取你的 TokenStore 实例
        FengRedisTokenStore tokenStore = SpringContextHolder.getBean(FengRedisTokenStore.class);
        if (tokenStore == null) {
            log.debug("未能从 Spring 容器中获取 FengRedisTokenStore 实例");
            return null;
        }
        return tokenStore.getAccessToken(oauth2Auth);
    }

    /**
     * 从当前请求的 Authorization Header 中获取 OAuth2AccessToken
     * <p>
     * 1. 从 RequestContextHolder 拿到 HttpServletRequest
     * 2. 解析出 Bearer token 字符串
     * 3. 从 Spring 容器中拿到 TokenStore（你的 FengRedisTokenStore）
     * 4. 调用 readAccessToken(tokenValue) 并返回结果
     * </p>
     */
    public OAuth2AccessToken getAccessTokenFromHeader() {
        // 1. 获取当前请求
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
        	// 如果attrs为空，那么很可能是因为本方法位于子线程中，而RequestContextHolder在主线程中初始化了，解决方案在控制器中新增两句，即ChatEndpoint.chat的前两句，这样就可以在主线程和子线程中共享。
            log.debug("无法获取 ServletRequestAttributes，无法读取请求头");
            return null;
        }
        HttpServletRequest request = attrs.getRequest();

        // 2. 解析 Authorization: Bearer xxx
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StrUtil.isBlank(header) || !header.startsWith(SecurityConstants.BEARER_TOKEN_TYPE)) {
            log.debug("请求头中未包含 Bearer token");
            return null;
        }
        String tokenValue = header.substring(SecurityConstants.BEARER_TOKEN_TYPE.length()).trim();

        // 3. 从 Spring 上下文中获取你的 TokenStore Bean
        TokenStore tokenStore = SpringContextHolder.getBean(TokenStore.class);
        if (tokenStore == null) {
            log.debug("从 Spring 容器中无法获取 TokenStore Bean");
            return null;
        }

        // 4. 读取并返回访问令牌
        try {
            return tokenStore.readAccessToken(tokenValue);
        } catch (InvalidTokenException e) {
            log.debug("无效的访问令牌: {}", tokenValue, e);
            return null;
        }
    }

}
