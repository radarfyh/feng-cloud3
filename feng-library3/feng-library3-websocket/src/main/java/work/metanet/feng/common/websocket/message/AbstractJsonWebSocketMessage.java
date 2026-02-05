package work.metanet.feng.common.websocket.message;

/**
 * @author edison 2022/09/15
 * @version 1.0
 */
public abstract class AbstractJsonWebSocketMessage implements JsonWebSocketMessage {

	public static final String TYPE_FIELD = "type";

	private final String type;

	protected AbstractJsonWebSocketMessage(String type) {
		this.type = type;
	}

	@Override
	public String getType() {
		return type;
	}

}
