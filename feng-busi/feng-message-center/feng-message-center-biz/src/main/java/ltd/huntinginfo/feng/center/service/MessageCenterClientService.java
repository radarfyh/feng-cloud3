//package ltd.huntinginfo.feng.center.service;
//
//import ltd.huntinginfo.feng.center.api.entity.MsgAppCredential;
//import ltd.huntinginfo.feng.center.api.entity.*;
//
//import java.util.List;
//
///**
// * 消息中心客户端服务接口
// * 负责与部级消息中心进行HTTP通信
// */
//public interface MessageCenterClientService {
//    
//    /**
//     * 申请消息编码
//     */
//    CodeApplyResponse applyMessageCode(MsgAppCredential credential, CodeApplyRequest request);
//    
//    /**
//     * 发送消息到消息中心
//     */
//    MessageSendResponse sendMessages(MsgAppCredential credential, List<MessageSendRequest> requests);
//    
//    /**
//     * 接收消息（轮询）
//     */
//    MessageReceiveResponse receiveMessages(MsgAppCredential credential, MessageReceiveRequest request);
//    
//    /**
//     * 更新消息状态
//     */
//    MessageStatusUpdateResponse updateMessageStatus(MsgAppCredential credential, MessageStatusUpdateRequest request);
//    
//    /**
//     * 查询用户未读消息（前20条）
//     */
//    UnreadMessageResponse queryUnreadMessages(MsgAppCredential credential, UnreadMessageRequest request);
//    
//   
//    /**
//     * 刷新Token（如果需要）
//     */
//    boolean refreshToken(MsgAppCredential credential);
//    
//    /**
//     * 获取当前Token是否有效
//     */
//    boolean isTokenValid(MsgAppCredential credential);
//    
//    /**
//     * 获取部级消息中心基础URL
//     */
//    String getBaseUrl();
//    
//    /**
//     * 获取是否使用桩代码
//     */
//    boolean isMockEnabled();
//
//	/**
//	 * 获取有效的Token
//	 */
//	String getValidToken(MsgAppCredential credential);
//}