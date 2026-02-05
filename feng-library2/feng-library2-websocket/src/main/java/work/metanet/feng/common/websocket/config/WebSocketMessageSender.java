package work.metanet.feng.common.websocket.config;

import cn.hutool.json.JSONUtil;
import work.metanet.feng.common.websocket.holder.WebSocketSessionHolder;
import work.metanet.feng.common.websocket.message.JsonWebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;

/**
 * @author edison 
 * @date 2022/09/15
 * @version 1.0
 */
@Slf4j
public class WebSocketMessageSender {

	public static void broadcast(String message) {
		Collection<WebSocketSession> sessions = WebSocketSessionHolder.getSessions();
		for (WebSocketSession session : sessions) {
			send(session, message);
		}
	}

	public static boolean send(Object sessionKey, String message) {
		WebSocketSession session = WebSocketSessionHolder.getSession(sessionKey);
		if (session == null) {
			//不管用户是否在线都推送消息
			log.info("[send] 当前 sessionKey：{} 对应 session 不在本服务中", sessionKey);
		}
			return send(session, message);
	}

	public static void send(WebSocketSession session, JsonWebSocketMessage message) {
		send(session, JSONUtil.toJsonStr(message));
	}

	public static boolean send(WebSocketSession session, String message) {
		if (session == null) {
			log.error("[send] session 为 null");
			return false;
		}
		if (!session.isOpen()) {
			log.error("[send] session 已经关闭");
			return false;
		}
		try {
			session.sendMessage(new TextMessage(message));
		}
		catch (IOException e) {
			log.error("[send] session({}) 发送消息({}) 异常", session, message, e);
			return false;
		}
		return true;
	}

}
