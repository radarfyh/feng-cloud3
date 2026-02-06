package work.metanet.feng.auth.handler;

import work.metanet.feng.admin.api.dto.SysLogDTO;
import work.metanet.feng.admin.api.feign.RemoteLogService;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.util.KeyStrResolver;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.log.util.LogTypeEnum;
import work.metanet.feng.common.log.util.SysLogUtils;
import work.metanet.feng.common.security.handler.AuthenticationLogoutHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
// 已移除对旧版 OAuth2Authentication 的导入
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import cn.hutool.json.JSONUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 退出事件处理器
 * <p>
 * 该类用于处理用户注销操作时记录退出日志。
 * </p>
 */
@Slf4j
@Component
@AllArgsConstructor
public class FengAuthenticationLogoutEventHandler implements AuthenticationLogoutHandler {

    private final RemoteLogService logService;
    private final KeyStrResolver tenantKeyStrResolver;

    /**
     * 处理用户注销操作
     * <p>
     * 该方法用于在用户注销时记录相关的退出日志，包括用户名、token、clientId、组织信息等。
     * </p>
     * 
     * @param authentication 登录的 Authentication 对象
     * @param request 请求对象
     * @param response 响应对象
     */
    @Async
    @Override
    public void handle(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        // 获取用户名
        String username = authentication.getName();
        SysLogDTO sysLog = SysLogUtils.getSysLog(request, username);
        // 1. 记录请求头（使用工具类方法）
        SysLogUtils.recordRequestHeaders(request, sysLog);
        // 2. 记录认证信息（使用工具类方法）
        SysLogUtils.recordAuthInfo(request, sysLog);
        
        sysLog.setTitle("用户:" + username + " 退出");
        sysLog.setType(LogTypeEnum.SECURITY.getType());
        
        // 3.记录响应数据
        SysLogUtils.recordResponseData(response, sysLog);

        // 注意：在新的 Spring Security OAuth2 Authorization Server 架构下，
        // 客户端信息（clientId）的获取方式已改变，此处暂时不记录，或需从其他上下文获取。
        // 可根据未来需要，从 Token 或 SecurityContext 中解析。
        sysLog.setServiceId("N/A"); // 或留空，或从其他地方获取

        // 设置租户信息 (增强异常处理)
        try {
            Integer tenantId = Integer.valueOf(tenantKeyStrResolver.key());
            sysLog.setTenantId(tenantId);
        } catch (NumberFormatException e) {
            log.error("Failed to parse tenant ID: {}", tenantKeyStrResolver.key(), e);
            sysLog.setTenantId(null); // 或设置一个默认值，如 0
        }

        // 获取退出的 token
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null) {
            sysLog.setParams(token);
        } else {
            sysLog.setParams("No token present");
            log.warn("No token found in request header for user: {}", username);
        }
        
        sysLog.setAppToken(token);
        // 保存日志
        logService.saveLog(sysLog, SecurityConstants.FROM_IN);

        // 记录退出日志
        log.info("用户：{} 退出成功, token: {} 已注销", username, token);
    }
}