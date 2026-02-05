package work.metanet.feng.common.log.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import work.metanet.feng.admin.api.dto.SysLogDTO;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.enums.BusinessEnum;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.SpringContextHolder;
import work.metanet.feng.common.log.wrapper.CachedBodyHttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 系统日志工具类
 */
@UtilityClass
@Slf4j
public class SysLogUtils {

    public SysLogDTO getSysLog(HttpServletRequest request, String username) {
        SysLogDTO sysLog = new SysLogDTO();
        
        // 从请求头获取租户ID
        Integer tenantId = -1;
        String strTenant = request.getHeader(CommonConstants.TENANT_HEADER_KEY);
        try {
            tenantId = Integer.valueOf(strTenant);
            sysLog.setTenantId(tenantId);
        } catch (Exception e) {
            log.error("getSysLog-->租户ID转换错误:{}", strTenant);
        }
        
        // username可能为空
        if (StrUtil.isNotBlank(username)) {
            sysLog.setCreateBy(username);
        }
        
        // 设置常用信息
        sysLog.setType(LogTypeEnum.NORMAL.getType());
        sysLog.setRemoteAddr(ServletUtil.getClientIP(request));
        sysLog.setRequestUri(request.getRequestURI());
        sysLog.setMethod(request.getMethod());
        sysLog.setUserAgent(request.getHeader("user-agent"));
        
        // 修改为获取spring.application.name作为服务ID
        String serviceId = SpringContextHolder.getApplicationContext().getEnvironment()
                .getProperty("spring.application.name");
        sysLog.setServiceId(serviceId);
        
        // 设置参数
        sysLog.setParams(HttpUtil.toParams(request.getParameterMap()));
        
        return sysLog;
    }

    public HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) Objects
            .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }
    
    public HttpServletResponse getCurrentResponse() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
    }

    /**
     * 记录认证信息（appKey, appSecret, token）
     */
    public SysLogDTO recordAuthInfo(HttpServletRequest request, SysLogDTO logDTO) {
        // 1. 从Header获取（优先）
        String appKey = request.getHeader("X-App-Key");
        String appSecret = request.getHeader("X-App-Secret");
        
        // 2. 从URL参数获取（次优）
        if (StrUtil.isBlank(appKey)) {
            appKey = request.getParameter("appKey");
            appSecret = request.getParameter("appSecret");
        }
        
        // 3. 从请求体JSON获取（最后尝试）
        String requestBody = "";
        if (StrUtil.isBlank(appKey)) {
            try {
//            	ServletInputStream inputStream = request.getInputStream();
//                requestBody = IOUtils.toString(inputStream, StandardCharsets.UTF_8); // 注：这句可能会抛出异常，因为inputStream只允许访问一次
            	requestBody = getSafeRequestBody(request);
            } catch (Exception e) {
                log.error("recordAuthInfo-->读取请求体失败: {}", e.getMessage());
            }
            try {
                if (StrUtil.isNotBlank(requestBody)) {
                    JSONObject json = JSONUtil.parseObj(requestBody);
                    appKey = json.getStr("appKey");
                    appSecret = json.getStr("appSecret");
                }
            } catch (Exception e) {
                log.error("recordAuthInfo-->从请求体解析认证信息失败: {}", e.getMessage());
            }
        }
        
        // 设置到DTO（脱敏处理）
        if (StrUtil.isNotBlank(appKey)) {
            logDTO.setAppKey(appKey);
            logDTO.setAppSecret(desensitizeSecret(appSecret));
        }
        
        // 记录认证信息
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            logDTO.setAppToken(authHeader.substring(7));
        }
        
        return logDTO;
    }

    /**
     * 记录请求头信息（排除敏感信息如Authorization）
     */
    public SysLogDTO recordRequestHeaders(HttpServletRequest request, SysLogDTO logDTO) {
        logDTO.setRequestHeader(JSONUtil.toJsonStr(Collections.list(request.getHeaderNames())
                .stream()
                .filter(name -> !name.equalsIgnoreCase("authorization"))
                .collect(Collectors.toMap(
                    Function.identity(), 
                    name -> request.getHeader(name)
                ))));
        
        return logDTO;
    }

    /**
     * 脱敏处理AppSecret（屏蔽1/3字符）
     */
    public String desensitizeSecret(String secret) {
        if (StrUtil.isBlank(secret)) {
            return "******";
        }
        
        int length = secret.length();
        int visibleChars = (int) Math.ceil(length * 2.0 / 3);
        int hiddenChars = length - visibleChars;
        
        return secret.substring(0, visibleChars) 
               + StrUtil.repeat('*', hiddenChars);
    }

    /**
     * 判断是否需要记录请求体（排除文件上传等大请求体）
     */
    public boolean shouldRecordRequestBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        return !(contentType != null && 
               (contentType.contains("multipart/form-data") || 
                contentType.contains("octet-stream")));
    }
    
    public SysLogDTO recordResponseData(HttpServletResponse response, SysLogDTO logDTO) {
	    // 设置默认状态和code
    	logDTO.setStatus(Integer.toString(response.getStatus()));
    	logDTO.setCode(Integer.toString(CommonConstants.SUCCESS)); // 默认使用成功code
	    logDTO.setStatusText(BusinessEnum.getMsgByCode(response.getStatus()));
	    
	    // 如果是RESTful API调用，尝试从响应中获取实际的code
	    try {
	        // 获取响应内容（需要确保响应是可读取的）
	        if (response instanceof ContentCachingResponseWrapper) {
	            String responseBody = new String(((ContentCachingResponseWrapper) response).getContentAsByteArray());
	            R<?> result = JSONUtil.toBean(responseBody, R.class);
	            if (result != null) {
	            	logDTO.setCode(Integer.toString(result.getCode()));
	            	logDTO.setMessage(result.getMsg());
	            	logDTO.setData(JSONUtil.toJsonPrettyStr(result.getData()));
	            }
	        }
	    } catch (Exception e) {
	        log.error("recordResponseData-->解析响应内容获取code失败，使用默认值,错误：{}", e);
	    }
	    
	    return logDTO;
    }
    
    public String getSafeRequestBody(HttpServletRequest request) {
        try {
            if (request instanceof CachedBodyHttpServletRequest) {
                return new String(((CachedBodyHttpServletRequest) request).getCachedBody());
            }
            log.warn("getSafeRequestBody-->请求体未缓存");
            return null;
        } catch (Exception e) {
        	log.error("getSafeRequestBody-->请求体读取失败：{}", e);
            return null;
        }
    }
}