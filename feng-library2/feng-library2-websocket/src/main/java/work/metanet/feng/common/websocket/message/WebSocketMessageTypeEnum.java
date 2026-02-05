package work.metanet.feng.common.websocket.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author edison 2022/09/15
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
public enum WebSocketMessageTypeEnum {

	PING("ping"), PONG("pong");

	private final String value;

}
