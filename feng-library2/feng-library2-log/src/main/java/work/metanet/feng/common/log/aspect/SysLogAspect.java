package work.metanet.feng.common.log.aspect;

import work.metanet.feng.admin.api.dto.SysLogDTO;
import work.metanet.feng.common.core.constant.enums.BusinessEnum;
import work.metanet.feng.common.core.util.KeyStrResolver;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.SpringContextHolder;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.common.log.event.SysLogEvent;
import work.metanet.feng.common.log.util.LogTypeEnum;
import work.metanet.feng.common.log.util.SysLogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

/**
 * 操作日志使用spring event异步入库
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class SysLogAspect {

    private final ApplicationEventPublisher publisher;

    private final Optional<KeyStrResolver> keyStrResolverOptional;

    @Around("@annotation(sysLog)")
    public Object around(ProceedingJoinPoint point, SysLog sysLog) {
        // 1. 初始化日志DTO
        String title = sysLog.value();    
        String type = sysLog.type();
        if (StrUtil.isBlank(type)) {
        	type = LogTypeEnum.NORMAL.getType();
        }
        
        // 2. 记录请求数据
        SysLogDTO logDTO = recordRequestData(point, title, type);
        
        // 3. 执行目标方法并记录响应
        Long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = point.proceed();
            if (logDTO != null) {
                recordResponseData(result, logDTO);
            }
        } catch (Throwable e) {
            handleException(logDTO, e);
            log.error("around-->point.proceed异常：{}", e.getMessage());
        } finally {
            // 4. 记录通用信息
            if (logDTO != null) {
                recordCommonInfo(logDTO, startTime);
            }
            publisher.publishEvent(new SysLogEvent(logDTO));
        }
        return result;
    }
    
    private void recordResponseData(Object result, SysLogDTO logDTO) {
        try {
            if (result instanceof R) {
                R<?> r = (R<?>) result;
                logDTO.setCode(String.valueOf(r.getCode()));
                logDTO.setMessage(r.getMsg());
                logDTO.setData(JSONUtil.toJsonStr(r.getData()));
            }
            
            // 记录响应头（需在Controller中设置）
            if (SysLogUtils.getCurrentResponse() != null) {
                logDTO.setHeader(JSONUtil.toJsonStr(SysLogUtils.getCurrentResponse().getHeaderNames()
                    .stream()
                    .collect(Collectors.toMap(
                        Function.identity(), 
                        SysLogUtils.getCurrentResponse()::getHeader
                    ))));
                logDTO.setStatus(Integer.toString(SysLogUtils.getCurrentResponse().getStatus()));
                logDTO.setStatusText(BusinessEnum.getMsgByCode(SysLogUtils.getCurrentResponse().getStatus()));
            }
        } catch (Exception e) {
            log.error("recordResponseData-->记录响应数据异常: {}", e.getMessage());
        }
    }

    private void handleException(SysLogDTO logDTO, Throwable e) {
        logDTO.setType(LogTypeEnum.ERROR.getType());
        logDTO.setException(e.getStackTrace().toString());
        logDTO.setMessage(e.getMessage());
        logDTO.setCode("1");
    }

    private void recordCommonInfo(SysLogDTO logDTO, Long startTime) {
        logDTO.setTime(System.currentTimeMillis() - startTime);
        logDTO.setResponseTime(LocalDateTime.now());
        
        keyStrResolverOptional.ifPresent(resolver -> {
            try {
                // 记录租户ID
                logDTO.setTenantId(Integer.valueOf(resolver.key()));
            } catch (Exception e) {
                log.warn("解析租户信息异常: {}", e.getMessage());
            }
            //logDTO.setServiceId(resolver.getClientId());
            // 记录用户名
            logDTO.setCreateBy(Objects.requireNonNull(resolver.getUsername()));
        });
    }
    
    private SysLogDTO recordRequestData(ProceedingJoinPoint point, String name, String type) {
        try {
            HttpServletRequest request = SysLogUtils.getCurrentRequest();
            SysLogDTO logDTO = SysLogUtils.getSysLog(request, "");
            logDTO.setTitle(name);
            logDTO.setType(type);
            
            // 1. 记录请求头（使用工具类方法）
            SysLogUtils.recordRequestHeaders(request, logDTO);
            
            // 2. 记录认证信息（使用工具类方法）
            SysLogUtils.recordAuthInfo(request, logDTO);

            // 3. 按需记录请求体（避免大请求体）
            if (SysLogUtils.shouldRecordRequestBody(request)) {
                Object[] args = point.getArgs();
                if (args != null && args.length > 0) {
                    logDTO.setRequestData(extractSafeRequestBody(args));
                }
            }
            
            return logDTO;
        } catch (Exception e) {
            log.error("recordRequestData-->记录请求数据异常: {}", e.getMessage());
            return null;
        }
    }

    private String extractSafeRequestBody(Object[] args) {
        try {
            return Arrays.stream(args)
                .filter(arg -> !(arg instanceof MultipartFile)) // 排除文件对象
                .findFirst()
                .map(JSONUtil::toJsonStr)
                .orElse(null);
        } catch (Exception e) {
            log.error("extractSafeRequestBody-->请求体序列化异常: {}", e.getMessage());
            return null;
        }
    }
}