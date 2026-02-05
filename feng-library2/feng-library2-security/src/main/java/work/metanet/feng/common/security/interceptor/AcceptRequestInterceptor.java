package work.metanet.feng.common.security.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 请求拦截器，用于设置请求头中的 "Accept" 字段，避免返回 XML 格式的响应。
 * 默认将 "Accept" 请求头设置为 "application/json"，确保服务器返回 JSON 格式的响应。
 * <p>
 * 可根据需要扩展更多内容类型的支持，例如通过构造函数注入不同的类型。
 * </p>
 */
@Slf4j
@Component
public class AcceptRequestInterceptor implements ClientHttpRequestInterceptor {

    /**
     * 拦截请求并修改请求头中的 "Accept" 字段，避免返回 XML 格式。
     * 默认将请求头中的 "Accept" 设置为 "application/json"。
     * 
     * @param request 当前的 HTTP 请求
     * @param body 请求体
     * @param execution 用于继续执行请求的执行器
     * @return 返回修改后的响应
     * @throws IOException 如果请求过程中发生 I/O 异常
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        
        // 检查请求头是否为空
        if (request.getHeaders() != null) {
            // 设置请求头的 Accept 字段，确保返回 JSON 格式
            request.getHeaders().set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            
            // 日志记录修改的请求头信息
            logRequestHeader(request);
        }

        // 执行请求并返回响应
        return execution.execute(request, body);
    }

    /**
     * 记录请求头信息的日志，便于调试和监控。
     * 
     * @param request 当前的 HTTP 请求
     */
    private void logRequestHeader(HttpRequest request) {
        if (request.getHeaders() != null) {
            String acceptHeader = request.getHeaders().getFirst(HttpHeaders.ACCEPT);
            if (acceptHeader != null) {
                // 输出修改后的 Accept 请求头
                log.debug("Modified Accept header to: {}", acceptHeader);
            }
        }
    }
}
