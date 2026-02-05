package work.metanet.feng.common.websocket.handler;

import work.metanet.feng.common.websocket.config.WebSocketMessageSender;
import work.metanet.feng.common.websocket.message.JsonWebSocketMessage;
import work.metanet.feng.common.websocket.message.PingJsonWebSocketMessage;
import work.metanet.feng.common.websocket.message.PongJsonWebSocketMessage;
import work.metanet.feng.common.websocket.message.WebSocketMessageTypeEnum;
import org.springframework.web.socket.WebSocketSession;

/**
 * 心跳处理，接收到客户端的ping时，立刻回复一个pong
 *
 * @author edison 2022/09/15
 * @version 1.0
 */
public class PingJsonMessageHandler implements JsonMessageHandler<PingJsonWebSocketMessage> {

	@Override
	public void handle(WebSocketSession session, PingJsonWebSocketMessage message) {
		JsonWebSocketMessage pongJsonWebSocketMessage = new PongJsonWebSocketMessage();
		WebSocketMessageSender.send(session, pongJsonWebSocketMessage);
	}

	@Override
	public String type() {
		return WebSocketMessageTypeEnum.PING.getValue();
	}

	@Override
	public Class<PingJsonWebSocketMessage> getMessageClass() {
		return PingJsonWebSocketMessage.class;
	}

}
