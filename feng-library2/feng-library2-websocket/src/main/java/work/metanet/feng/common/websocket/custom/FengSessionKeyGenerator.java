package work.metanet.feng.common.websocket.custom;

import work.metanet.feng.common.websocket.holder.SessionKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

/**
 * @author edison 
 * @date 2022/09/15
 * websocket session 标识生成规则
 */
@Configuration
@RequiredArgsConstructor
public class FengSessionKeyGenerator implements SessionKeyGenerator {

    /**
     * 获取当前session的唯一标识
     *
     * @param webSocketSession 当前session
     * @return session唯一标识
     */
    @Override
    public Object sessionKey(WebSocketSession webSocketSession) {

        Object obj = webSocketSession.getAttributes().get("USER_KEY_ATTR_NAME");

        if (Objects.nonNull(obj)) {
            //userId
            return String.valueOf(obj);
        }

        return null;
    }

}
