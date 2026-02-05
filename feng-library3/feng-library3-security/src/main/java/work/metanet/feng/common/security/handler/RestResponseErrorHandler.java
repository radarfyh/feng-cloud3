package work.metanet.feng.common.security.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import work.metanet.feng.common.core.constant.enums.BusinessEnum;

import java.io.IOException;

/**
 * 自定义响应失败处理器，用于覆盖默认的 400 错误处理行为。
 * <p>
 * 本类重写了 Spring 的默认错误处理器，将 HTTP 状态码为 400 (Bad Request) 的响应不作为异常处理，其他非 400 错误则交由默认错误处理器处理。
 * </p>
 */
public class RestResponseErrorHandler extends DefaultResponseErrorHandler {

    /**
     * 处理错误响应，重写默认的错误处理机制，忽略 400 错误
     * 
     * @param response 客户端响应
     * @throws IOException 如果读取响应时发生 I/O 错误
     */
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        // 如果状态码不是 400，交由默认的错误处理逻辑
        if (response.getRawStatusCode() != BusinessEnum.WEB_BAD_REQUEST.getCode()) {
            super.handleError(response);
        } else {
            // 对于 400 错误，不抛出异常，允许继续执行
            // 这里可以添加其他的逻辑处理，例如日志记录等
        }
    }
}

