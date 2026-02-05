package work.metanet.feng.common.websocket.message;

/**
 * @author edison 2022/09/15
 * @version 1.0
 */
public class PingJsonWebSocketMessage extends AbstractJsonWebSocketMessage {

	public PingJsonWebSocketMessage() {
		super(WebSocketMessageTypeEnum.PING.getValue());
	}

}
