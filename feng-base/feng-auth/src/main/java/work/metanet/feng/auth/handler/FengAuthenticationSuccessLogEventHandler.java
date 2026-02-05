package work.metanet.feng.auth.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import work.metanet.feng.admin.api.dto.SysLogDTO;
import work.metanet.feng.admin.api.feign.RemoteLogService;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.constant.enums.BusinessEnum;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.WebUtils;
import work.metanet.feng.common.log.util.LogTypeEnum;
import work.metanet.feng.common.log.util.SysLogUtils;
import work.metanet.feng.common.security.handler.AuthenticationSuccessHandler;
//import work.metanet.feng.log2.api.feign.RemoteLogReceptionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 登录成功日志记录处理器
 * <p>
 * 该类用于处理用户登录成功事件，记录登录日志并推送到 Kafka 或直接保存到数据库。
 * </p>
 */
@Slf4j
@Component
@AllArgsConstructor
public class FengAuthenticationSuccessLogEventHandler implements AuthenticationSuccessHandler {
    private static final String LOG_SERVICE_TYPE_LOGIN = "login";
    private static final String HEADER_PLATFORM_TYPE = "PlatformType";
    
    private final RemoteLogService remoteLogService;

    /**
     * 处理登录成功事件
     * <p>
     * 该方法在用户登录成功后触发，记录登录日志并推送到 Kafka 或直接保存到数据库。
     * </p>
     *
     * @param authentication 认证信息，包含用户详情
     * @param request HTTP 请求
     * @param response HTTP 响应
     */
    @Async
    @Override
    public void handle(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        try {
            String username = authentication.getName();
            SysLogDTO sysLog = SysLogUtils.getSysLog(request, username);
            // 1. 记录请求头（使用工具类方法）
            SysLogUtils.recordRequestHeaders(request, sysLog);
            // 2. 记录认证信息（使用工具类方法）
            SysLogUtils.recordAuthInfo(request, sysLog);
            sysLog.setType(LogTypeEnum.SECURITY.getType());
            sysLog.setTitle("用户："+ username + " 登录成功");

            // 3.记录响应数据
            SysLogUtils.recordResponseData(response, sysLog);
            
            log.debug("保存登录日志：{}", JSONUtil.toJsonStr(sysLog));

            // 推送日志到 Kafka，暂时禁用 20250226
            //JSONObject jsonObject = buildLogJson(authentication, request, sysLog);
            //remoteLogReceptionService.saveLog(jsonObject);

            // 直接保存到数据库
            remoteLogService.saveLog(sysLog, SecurityConstants.FROM_IN);

            log.info("用户：{} 登录成功", username);
        } catch (Exception e) {
            log.error("处理登录成功事件失败，错误：{}", e);
        }
    }
    /**
     * 构建日志 JSON 对象
     *
     * @param authentication 认证信息
     * @param request HTTP 请求
     * @param sysLog 系统日志 DTO
     * @return 日志 JSON 对象
     */
    private JSONObject buildLogJson(Authentication authentication, HttpServletRequest request, SysLogDTO sysLog) {
        JSONObject jsonObject = JSONUtil.createObj();
        String username = authentication.getName();
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String platformType = request.getHeader(HEADER_PLATFORM_TYPE);
        String now = DateUtil.now();

        jsonObject.set("logServiceType", LOG_SERVICE_TYPE_LOGIN)
                 .set("logStatus", LogTypeEnum.NORMAL.getType())
                 .set("title", username + "用户登录")
                 .set("remoteAddr", sysLog.getRemoteAddr())
                 .set("username", username)
                 .set("time", sysLog.getTime())
                 .set("platformType", platformType)
                 .set("createTime", now)
                 .set("serviceId", WebUtils.extractClientId(header).orElse("N/A"))
                 .set("tenantId", sysLog.getTenantId());

        return jsonObject;
    }
}
