package work.metanet.feng.common.websocket.distribute;

/**
 * 消息分发器
 *
 * @author edison 
 * @date 2022/09/15
 * @version 1.0
 */
public interface MessageDistributor {

	/**
	 * 消息分发
	 * @param messageDO 发送的消息
	 */
	void distribute(MessageDO messageDO);

}
