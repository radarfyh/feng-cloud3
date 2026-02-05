package work.metanet.feng.auth.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.gray.support.NonWebVersionContextHolder;
/*
 * VersionInterceptor 拦截器，用于处理版本信息的获取与存储。
 * <p>
 * 本拦截器通过获取请求头中的版本信息（VERSION），并将其存储在 NonWebVersionContextHolder 中。
 * 在请求完成后，清除存储的版本信息。此拦截器主要用于管理非Web请求的版本信息。
 * </p>
 * <p>
 * 解决WebUtils.getRequest()如下报错：No thread-bound request found: Are you referring to request attributes outside of an actual web request, 
 * or processing a request outside of the originally receiving thread? If you are actually operating within a web request and still receive this message, 
 * your code is probably running outside of DispatcherServlet: In this case, use RequestContextListener or RequestContextFilter to expose the current request.
 * </p>
 */
@Slf4j
@Component
public class VersionInterceptor implements HandlerInterceptor {

    /**
     * 在请求处理之前，获取版本信息并存储到 NonWebVersionContextHolder 中。
     * <p>
     * 该方法会从请求头中获取"VERSION"字段，如果字段存在且非空，则将其存储。
     * </p>
     *
     * @param request 当前请求对象
     * @param response 当前响应对象
     * @param handler 处理器
     * @return 返回true表示继续处理请求，false表示请求被拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取版本信息
        String version = request.getHeader(CommonConstants.VERSION);
        if (StrUtil.isNotBlank(version)) {
            // 如果版本信息存在且非空，存储到 NonWebVersionContextHolder 中
            NonWebVersionContextHolder.setVersion(version);
        } else {
            // 如果没有版本信息，设置一个默认版本
            // NonWebVersionContextHolder.setVersion(CommonConstants.DEFAULT_VERSION);
        }
        return true;
    }

    /**
     * 请求处理完成后，清理存储的版本信息。
     * <p>
     * 此方法会在请求完成后被调用，负责清理 NonWebVersionContextHolder 中存储的版本信息，
     * 以避免内存泄漏或版本信息在不同请求间的污染。
     * </p>
     *
     * @param request 当前请求对象
     * @param response 当前响应对象
     * @param handler 处理器
     * @param ex 异常对象，如果发生异常，传递异常信息
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理 NonWebVersionContextHolder 中的版本信息
        NonWebVersionContextHolder.clear();
        
        // 可以在这里对异常进行处理，如果有需要的话
        if (ex != null) {
            // 记录异常日志，或者进行其他异常处理操作
            log.error("Request processing failed", ex);
        }
    }
}