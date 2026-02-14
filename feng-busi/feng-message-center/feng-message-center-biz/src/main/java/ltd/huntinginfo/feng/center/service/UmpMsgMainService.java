package ltd.huntinginfo.feng.center.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import ltd.huntinginfo.feng.center.api.dto.MessageSendDTO;
import ltd.huntinginfo.feng.center.api.dto.MessageQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.MessageDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MessagePageVO;
import ltd.huntinginfo.feng.center.api.vo.MessageStatisticsVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息主表服务接口
 */
public interface UmpMsgMainService extends IService<UmpMsgMain> {

    /**
     * 创建消息
     *
     * @param sendDTO 消息发送DTO
     * @return 消息ID
     */
    String createMessage(MessageSendDTO sendDTO);

    /**
     * 创建代理消息
     *
     * @param sendDTO 消息发送DTO
     * @param agentAppKey 代理平台标识
     * @param agentMsgId 代理消息ID
     * @return 消息ID
     */
    String createAgentMessage(MessageSendDTO sendDTO, String agentAppKey, String agentMsgId);

    /**
     * 根据消息编码查询消息详情
     *
     * @param msgCode 消息编码
     * @return 消息详情VO
     */
    MessageDetailVO getMessageByCode(String msgCode);

    /**
     * 分页查询消息
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<MessagePageVO> queryMessagePage(MessageQueryDTO queryDTO);

    /**
     * 更新消息状态
     *
     * @param msgId 消息ID
     * @param status 目标状态
     * @return 是否成功
     */
    boolean updateMessageStatus(String msgId, String status);

    /**
     * 批量更新消息状态
     *
     * @param msgIds 消息ID列表
     * @param status 目标状态
     * @return 成功更新数量
     */
    int batchUpdateMessageStatus(List<String> msgIds, String status);

    /**
     * 更新消息的已读统计
     *
     * @param msgId 消息ID
     * @param readCount 已读人数
     * @return 是否成功
     */
    boolean updateReadStatistics(String msgId, int readCount);

    /**
     * 处理过期消息
     *
     * @return 处理的过期消息数量
     */
    int processExpiredMessages();

    /**
     * 获取消息统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param appKey 应用标识（可选）
     * @return 统计信息VO
     */
    MessageStatisticsVO getMessageStatistics(LocalDateTime startTime, LocalDateTime endTime, String appKey);

    /**
     * 根据接收者ID查询未读消息
     *
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param limit 限制数量
     * @return 未读消息列表
     */
    List<MessageDetailVO> getUnreadMessages(String receiverId, String receiverType, int limit);

    /**
     * 检查消息是否存在且有效
     *
     * @param msgId 消息ID
     * @return 是否存在且未删除
     */
    boolean existsAndValid(String msgId);
    
    /**
     * 更新接收者统计信息
     */
    boolean updateReceiverCount(String messageId, Integer totalReceivers, 
                               Integer receivedCount, Integer readCount);
    /**
     * 查询未读消息
     */
	List<MessageDetailVO> getAllUnreadMessages(int limit);
}
