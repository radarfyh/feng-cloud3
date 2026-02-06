package work.metanet.feng.common.security.handler;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

/**
 * 重写 ResponseErrorHandler，处理 RestTemplate 请求异常
 */
public class RestResponseErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        // Spring 6: getRawStatusCode() is removed, use getStatusCode().value()
        return new DefaultResponseErrorHandler().hasError(response);
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        // Spring 6: getRawStatusCode() is removed
        if (response.getStatusCode().value() >= 400) {
            // 这里可以添加自定义的异常处理逻辑
            // 例如读取响应体，抛出自定义异常等
            super.handleError(url, method, response);
        }
    }
}
