import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import work.metanet.feng.common.core.constant.enums.BusinessEnum;

import java.io.IOException;

@Slf4j
public class RestResponseErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        int statusCode = response.getRawStatusCode();
        
        // 如果状态码不是 400，交由默认的错误处理逻辑
        if (statusCode != BusinessEnum.WEB_BAD_REQUEST.getCode()) {
            super.handleError(response);
        } else {
            // 对于 400 错误，不抛出异常，允许继续执行
            // 记录日志以便调试
            log.debug("HTTP 400 (Bad Request) received and ignored by custom error handler. " +
                     "This may be expected business logic.");
        }
    }
}