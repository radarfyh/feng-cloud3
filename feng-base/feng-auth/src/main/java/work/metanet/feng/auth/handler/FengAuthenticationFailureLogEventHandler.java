package work.metanet.feng.auth.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import work.metanet.feng.admin.api.dto.SysLogDTO;
import work.metanet.feng.admin.api.feign.RemoteLogService;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.WebUtils;
import work.metanet.feng.common.log.util.LogTypeEnum;
import work.metanet.feng.common.log.util.SysLogUtils;
import work.metanet.feng.common.security.handler.AuthenticationFailureHandler;
//import work.metanet.feng.log2.api.feign.RemoteLogReceptionService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 登录失败日志记录处理器
 * <p>
 * 该类用于处理登录失败事件，记录登录失败的相关日志。
 * </p>
 */
@Slf4j
@Component
@AllArgsConstructor
public class FengAuthenticationFailureLogEventHandler implements AuthenticationFailureHandler {

//    private final RemoteLogReceptionService remoteLogReceptionService;
	private final RemoteLogService remoteLogService;

    /**
     * 异步处理登录失败事件
     * <p>
     * 该方法用于在登录失败时记录相关信息，并将其异步保存到日志系统。
     * </p>
     *
     * @param authenticationException 登录异常信息
     * @param authentication 登录的 Authentication 对象
     * @param request 请求对象
     * @param response 响应对象
     */
    @Async
    @Override
    @SneakyThrows
    public void handle(AuthenticationException authenticationException, Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        String username = authentication.getName();

        // 获取用户日志信息
        SysLogDTO sysLog = SysLogUtils.getSysLog(request, username);

        // 1. 记录请求头（使用工具类方法）
        SysLogUtils.recordRequestHeaders(request, sysLog);
        // 2. 记录认证信息（使用工具类方法）
        SysLogUtils.recordAuthInfo(request, sysLog);
        
        sysLog.setException(authenticationException.getLocalizedMessage());
        sysLog.setType(LogTypeEnum.SECURITY.getType());
        sysLog.setTitle("用户：" + username + " 登录失败");

        // 3.记录响应数据
        SysLogUtils.recordResponseData(response, sysLog);
        
//        // 创建日志对象
//        JSONObject jsonObject = JSONUtil.createObj();
//        // 获取请求头信息
//        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
//        String platformType = request.getHeader("PlatformType");
//        String now = DateUtil.now(); // 当前时间
//
//        // 设置日志信息
//        jsonObject.set("logServiceType", "login");
//        jsonObject.set("logStatus", LogTypeEnum.ERROR.getType());
//        jsonObject.set("title", username + "用户登录");
//        jsonObject.set("remoteAddr", sysLog.getRemoteAddr());
//        jsonObject.set("username", username);
//        jsonObject.set("time", sysLog.getTime());
//        jsonObject.set("exception", authenticationException.getLocalizedMessage());
//        jsonObject.set("platformType", platformType);
//        jsonObject.set("createTime", now);
//        jsonObject.set("serviceId", WebUtils.extractClientId(header).orElse("N/A"));
//        jsonObject.set("tenantId", sysLog.getTenantId());
//
//        // 将日志保存到远程日志接收服务
//        remoteLogReceptionService.saveLog(jsonObject);
        
        remoteLogService.saveLog(sysLog, SecurityConstants.FROM_IN);

        // 输出登录失败信息到日志
        log.info("用户：{} 登录失败，异常：{}", username, authenticationException.getLocalizedMessage());
    }
}
