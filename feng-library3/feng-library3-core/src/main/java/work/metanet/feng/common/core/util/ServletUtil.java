package work.metanet.feng.common.core.util;

import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import work.metanet.feng.common.core.constant.CommonConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ServletUtil {

    @SneakyThrows
    public static void write(HttpServletResponse response, R data) {
        response.setStatus(data.getCode());
        response.setHeader("Content-type", "application/json;charset=" + CommonConstants.UTF_8);
        response.setCharacterEncoding(CommonConstants.UTF_8);
        response.getWriter().write(JSONUtil.toJsonStr(data));
    }

    @SneakyThrows
    public static void write(HttpServletResponse response, int status, R data) {
        response.setStatus(status);
        response.setHeader("Content-type", "application/json;charset=" + CommonConstants.UTF_8);
        response.setCharacterEncoding(CommonConstants.UTF_8);
        response.getWriter().write(JSONUtil.toJsonStr(data));
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    public static String getAuthorizationToken() {
        String token = getRequest().getHeader("Authorization");
        if (token != null && token.toLowerCase().startsWith("bearer")) {
            return token.toLowerCase().replace("bearer", "").trim();
        }
        return null;
    }
    
    public static String getHeader(String key) {
        String value = getRequest().getHeader(key);
        if (value != null) {
            return value.toLowerCase().trim();
        }
        return null;
    }

    public static String getToken(String token) {
        if (token != null && token.toLowerCase().startsWith("bearer")) {
            return token.replace("bearer", "").trim();
        }
        return token;
    }

    public static String getIpAddr() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "unknown";
        } else {
            String ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }

            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Forwarded-For");
            }

            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }

            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }

            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            
            if (ip == null || ip.isEmpty())
            {
            	ip = "unknown";
            }

            if ("0:0:0:0:0:0:0:1".equals(ip)) {
                ip = "127.0.0.1";
            }

            if (ip.contains(",")) {
                ip = ip.split(",")[0];
            }
            return ip;
        }
    }
}