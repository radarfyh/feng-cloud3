package work.metanet.feng.common.websocket.distribute;

/**
 * 本地消息分发，直接进行发送
 *
 * @author edison 
 * @date 2022/09/15
 * @version 1.0
 */
public class LocalMessageDistributor implements MessageDistributor, MessageSender {

	/**
	 * 消息分发
	 * @param messageDO 发送的消息
	 */
	@Override
	public void distribute(MessageDO messageDO) {
		doSend(messageDO);
	}

}
