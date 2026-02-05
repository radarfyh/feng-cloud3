package work.metanet.feng.common.log.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import work.metanet.feng.common.log.wrapper.CachedBodyHttpServletRequest;

import java.io.IOException;

@WebFilter(urlPatterns = "/*")
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // 通过注解控制顺序
public class CacheRequestBodyFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // 跳过文件上传等大请求体
        if (httpRequest.getContentLength() > 1024 * 1024) { // 1MB阈值
            chain.doFilter(request, response);
            return;
        }

        // 包装可重复读取的Request
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(httpRequest);
        chain.doFilter(cachedRequest, response);
    }
}
