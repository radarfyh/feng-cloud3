

package work.metanet.feng.common.core.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import work.metanet.feng.common.core.exception.CustomRuntimeException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * WebUtils类提供了用于Web应用程序中的常见操作的工具方法，涵盖了cookie操作、IP获取、请求和响应处理等功能。
 * <p>
 * 主要功能包括：
 * <ul>
 *     <li>获取和设置cookie</li>
 *     <li>获取客户端IP地址</li>
 *     <li>渲染JSON响应</li>
 *     <li>提取Basic Auth中的客户端ID</li>
 *     <li>判断是否为AJAX请求</li>
 * </ul>
 * </p>
 * <p>
 * 本类的主要用途是封装常见的Web相关操作，以便于代码的复用和减少重复代码，提高开发效率。
 * </p>
 */
@Slf4j
@UtilityClass
public class WebUtils extends org.springframework.web.util.WebUtils {

    private final String BASIC_ = "Basic ";
    private final String UNKNOWN = "unknown";

    /**
     * 判断是否为Ajax请求
     * <p>
     * 该方法检查请求是否标注了 @ResponseBody 或者 @RestController 注解，适用于判断是否返回 JSON。
     * </p>
     *
     * @param handlerMethod 处理方法
     * @return 是否为Ajax请求
     */
    public boolean isBody(HandlerMethod handlerMethod) {
        ResponseBody responseBody = ClassUtils.getAnnotation(handlerMethod, ResponseBody.class);
        return responseBody != null;
    }

    /**
     * 获取指定名称的cookie值
     * <p>
     * 该方法从当前请求的cookie中获取指定名称的cookie的值，如果cookie为空，则返回null。
     * </p>
     *
     * @param name cookie的名称
     * @return cookie的值
     */
    public String getCookieVal(String name) {
        HttpServletRequest request = getRequest();
        Assert.notNull(request, "Request from RequestContextHolder is null");
        return getCookieVal(request, name);
    }

    /**
     * 获取指定名称的cookie值
     * <p>
     * 该方法从指定的request对象中获取cookie的值。
     * </p>
     *
     * @param request HttpServletRequest 请求对象
     * @param name    cookie的名称
     * @return cookie的值
     */
    public String getCookieVal(HttpServletRequest request, String name) {
        Assert.notNull(request, "HttpServletRequest is null");
        Cookie cookie = getCookie(request, name);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * 删除指定名称的cookie
     * <p>
     * 该方法通过设置maxAge为0来删除指定名称的cookie。
     * </p>
     *
     * @param response HttpServletResponse 响应对象
     * @param key      cookie的键
     */
    public void removeCookie(HttpServletResponse response, String key) {
        setCookie(response, key, null, 0);
    }

    /**
     * 设置cookie
     * <p>
     * 该方法用于设置指定名称和值的cookie，并且设置cookie的有效期。
     * </p>
     *
     * @param response          HttpServletResponse 响应对象
     * @param name              cookie的名称
     * @param value             cookie的值
     * @param maxAgeInSeconds  cookie的有效期，单位：秒
     */
    public void setCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /**
     * 获取当前请求对象
     * <p>
     * 该方法从RequestContextHolder中获取当前的HttpServletRequest对象。
     * </p>
     *
     * @return 当前的HttpServletRequest对象
     */
    public HttpServletRequest getRequest() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            if (requestAttributes == null) {
                log.warn("getRequest: RequestAttributes为空，可能不在Web请求上下文中");
                return null;
            }
            
            if (requestAttributes instanceof ServletRequestAttributes) {
                return ((ServletRequestAttributes) requestAttributes).getRequest();
            } else {
                log.warn("getRequest: 当前请求属性不是ServletRequestAttributes类型: {}", 
                        requestAttributes.getClass().getName());
                return null;
            }
        } catch (IllegalStateException e) {
            log.warn("getRequest: 无法从RequestContextHolder获取HttpServletRequest", e);
            return null;
        }
    }

    /**
     * 获取当前响应对象
     * <p>
     * 该方法从RequestContextHolder中获取当前的HttpServletResponse对象。
     * 注意：在非Web请求线程中调用此方法将返回null。
     * </p>
     *
     * @return 当前的HttpServletResponse对象，如果无法获取则返回null
     */
    public HttpServletResponse getResponse() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                log.warn("无法获取当前请求属性，可能不在Web请求上下文中");
                return null;
            }
            
            if (requestAttributes instanceof ServletRequestAttributes) {
                return ((ServletRequestAttributes) requestAttributes).getResponse();
            } else {
                log.warn("当前请求属性不是ServletRequestAttributes类型: {}", 
                        requestAttributes.getClass().getName());
                return null;
            }
        } catch (IllegalStateException e) {
            log.error("从RequestContextHolder获取HttpServletResponse失败", e);
            return null;
        }
    }

    /**
     * 返回JSON格式的响应
     * <p>
     * 该方法用于将对象转换为JSON格式并返回给客户端。
     * </p>
     *
     * @param response HttpServletResponse 响应对象
     * @param result   结果对象
     */
    public void renderJson(HttpServletResponse response, Object result) {
        renderJson(response, result, MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * 返回指定contentType的JSON响应
     * <p>
     * 该方法用于将对象转换为指定contentType的JSON格式并返回给客户端。
     * </p>
     *
     * @param response   HttpServletResponse 响应对象
     * @param result     结果对象
     * @param contentType contentType类型
     */
    public void renderJson(HttpServletResponse response, Object result, String contentType) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(contentType);
        try (PrintWriter out = response.getWriter()) {
            out.append(JSONUtil.toJsonStr(result));
        } catch (IOException e) {
            log.error("Error occurred while rendering JSON response", e);
            renderErrorJson(response, "Internal server error occurred");
        }
    }

    /**
     * 返回错误的JSON响应
     * <p>
     * 该方法用于返回错误信息的JSON格式响应。
     * </p>
     *
     * @param response HttpServletResponse 响应对象
     * @param message  错误信息
     */
    private void renderErrorJson(HttpServletResponse response, String message) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try (PrintWriter out = response.getWriter()) {
            out.append("{\"error\": \"" + message + "\"}");
        } catch (IOException e) {
            log.error("Error occurred while rendering error JSON response", e);
        }
    }

    /**
     * 获取请求IP地址
     * <p>
     * 该方法尝试从请求头中获取IP地址，支持多层代理的情况下获取真实IP。
     * </p>
     *
     * @return 请求的IP地址
     */
    public String getIP() {
        return getIP(WebUtils.getRequest());
    }

    /**
     * 获取请求IP地址
     * <p>
     * 该方法尝试从请求头中获取IP地址，支持多层代理的情况下获取真实IP。
     * </p>
     *
     * @param request HttpServletRequest 请求对象
     * @return 请求的IP地址
     */
    public String getIP(HttpServletRequest request) {
        Assert.notNull(request, "HttpServletRequest is null");
        String ip = extractIpFromHeaders(request);
        if (StrUtil.isBlank(ip)) {
            ip = request.getRemoteAddr();
        }

        if (StrUtil.isBlank(ip)) {
            log.warn("Unable to retrieve a valid IP address");
        }
        return StrUtil.isBlank(ip) ? null : ip.split(",")[0];
    }

    /**
     * 从请求头中提取IP地址
     * <p>
     * 该方法从常见的HTTP请求头中依次查找IP地址，直到找到有效的IP地址为止。常见的HTTP头字段包括：
     * X-Requested-For, X-Forwarded-For, Proxy-Client-IP, WL-Proxy-Client-IP, HTTP_CLIENT_IP, HTTP_X_FORWARDED_FOR。
     * 如果在这些头字段中找到有效的IP地址，则返回该IP地址；否则返回null。
     * </p>
     *
     * @param request HttpServletRequest 请求对象，用于获取请求头
     * @return 从请求头中提取的有效IP地址，如果没有找到有效IP地址，则返回null
     */
private String extractIpFromHeaders(HttpServletRequest request) {
    String[] headerNames = {
        "X-Requested-For",
        "X-Forwarded-For", 
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_CLIENT_IP",
        "HTTP_X_FORWARDED_FOR"
    };
    
    for (String header : headerNames) {
        String ip = request.getHeader(header);
        if (isValidIp(ip)) {
            return ip;
        }
    }
    return null;
}

    
    /**
     * 从请求头中获取IP地址
     * @param request HttpServletRequest 请求对象
     * @param header  请求头名
     * @return 获取到的IP地址
     */
    private String getHeaderIp(HttpServletRequest request, String header) {
        return request.getHeader(header);
    }
    
    /**
     * 判断IP地址是否合法
     * <p>
     * 该方法用于验证IP地址是否为空或者是否为"unknown"。如果IP地址为空或为"unknown"，则认为它是无效的。
     * </p>
     *
     * @param ip 待验证的IP地址
     * @return 如果IP地址有效，则返回true；如果IP地址无效，则返回false
     */
    private boolean isValidIp(String ip) {
        return StrUtil.isNotBlank(ip) && !UNKNOWN.equalsIgnoreCase(ip);
    }

    /**
     * 解析client id
     * <p>
     * 该方法通过解析请求头中的认证信息来获取客户端ID。
     * </p>
     *
     * @param header 请求头中的Basic认证信息
     * @param defVal 解析失败时的默认值
     * @return 客户端ID
     */
    public String extractClientId(String header, final String defVal) {
        if (header == null || !header.startsWith(BASIC_)) {
            log.debug("Invalid Basic Auth header: {}", header);
            return defVal;
        }

        String token = decodeBasicAuthToken(header);
        if (token == null) {
            log.debug("Failed to decode Basic Auth token: {}", header);
            return defVal;
        }

        int delim = token.indexOf(":");
        if (delim == -1) {
            log.debug("Invalid Basic Auth token format: {}", header);
            return defVal;
        }

        return token.substring(0, delim);
    }

    /**
     * 解码Basic Auth认证的token
     * <p>
     * 该方法从传入的Authorization头中提取Base64编码的token（去除"Basic "前缀），
     * 然后解码并返回解码后的字符串（通常是客户端ID和密码的组合）。
     * 如果解码失败（例如非法的Base64格式），则返回null。
     * </p>
     *
     * @param header 传入的Authorization头部值，通常为"Basic <base64Token>"
     * @return 解码后的字符串，如果解码失败则返回null
     */
    private String decodeBasicAuthToken(String header) {
        // 提取Base64编码的token部分，去除"Basic "前缀
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        try {
            // 解码Base64编码的token
            byte[] decoded = Base64.decode(base64Token);
            // 将解码后的字节数组转化为字符串并返回
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // 如果解码失败，记录日志并返回null
            log.debug("Failed to decode Basic Auth token: {}", header);
            return null;
        }
    }
    
    /**
     * 从请求头中提取 client id
     * <p>
     * 该方法通过调用 `extractClientId` 方法并传入 `null` 作为默认值，
     * 返回一个包装了 `clientId` 的 `Optional` 对象。`Optional` 可以有效避免 null 值的处理问题。
     * 如果 `extractClientId` 方法未能成功提取 `clientId`，将返回一个空的 `Optional`。
     * </p>
     *
     * @param header 请求头，包含 Basic Authentication 信息
     * @return 包装了 `clientId` 的 `Optional` 对象，如果提取失败则返回一个空的 `Optional`
     */
    public Optional<String> extractClientId(String header) {
        return Optional.ofNullable(extractClientId(header, null));
    }

	
    /**
     * 从request 获取CLIENT_ID
     * <p>
     * 该方法从请求头中获取客户端ID，如果获取失败则抛出自定义异常。
     * </p>
     *
     * @return 客户端ID
     */
    public String getClientId(String header) {
        String clientId = extractClientId(header, null);
        if (clientId == null) {
            throw new CustomRuntimeException("Invalid basic authentication token");
        }
        return clientId;
    }
}

