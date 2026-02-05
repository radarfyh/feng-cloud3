package work.metanet.feng.common.websocket.custom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * @author edison
 * @date 2022/09/15
 */
@Slf4j
public class UserAttributeHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * Invoked before the handshake is processed.
     *
     * @param request    the current request
     * @param response   the current response
     * @param wsHandler  the target WebSocket handler
     * @param attributes the attributes from the HTTP handshake to associate with the
     *                   WebSocket session; the provided attributes are copied, the original map is not
     *                   used.
     * @return whether to proceed with the handshake ({@code true}) or abort
     * ({@code false})
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("握手前请求连接URL：" + request.getURI());
        String userId = ((ServletServerHttpRequest) request).getServletRequest().getParameter("userId");
        log.info("用户：{},建立连接...", userId);
        attributes.put("USER_KEY_ATTR_NAME", userId);
        return true;
    }

    /**
     * Invoked after the handshake is done. The response status and headers indicate the
     * results of the handshake, i.e. whether it was successful or not.
     *
     * @param request   the current request
     * @param response  the current response
     * @param wsHandler the target WebSocket handler
     * @param exception an exception raised during the handshake, or {@code null} if none
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

}
