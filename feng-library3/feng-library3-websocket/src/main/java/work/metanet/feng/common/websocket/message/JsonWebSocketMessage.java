package work.metanet.feng.common.websocket.message;

/**
 * @author edison 2022/09/15
 * @version 1.0
 */
public interface JsonWebSocketMessage {

	/**
	 * 消息类型，主要用于匹配对应的消息处理器
	 * @return 当前消息类型
	 */
	String getType();

}
