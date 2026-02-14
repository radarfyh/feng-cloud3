package ltd.huntinginfo.feng.center.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgBroadcast;
import ltd.huntinginfo.feng.center.api.dto.BroadcastQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastDetailVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastPageVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastStatisticsVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 广播信息筒表服务接口
 */
public interface UmpMsgBroadcastService extends IService<UmpMsgBroadcast> {

    /**
     * 创建广播记录
     *
     * @param msgId 消息ID
     * @param broadcastType 广播类型
     * @param targetScope 目标范围配置
     * @param targetDescription 目标范围描述
     * @return 广播记录ID
     */
    String createBroadcast(String msgId, String broadcastType,
                          Map<String, Object> targetScope, String targetDescription);

    /**
     * 根据消息ID查询广播记录
     *
     * @param msgId 消息ID
     * @return 广播记录详情VO
     */
    BroadcastDetailVO getBroadcastByMsgId(String msgId);

    /**
     * 分页查询广播记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<BroadcastPageVO> queryBroadcastPage(BroadcastQueryDTO queryDTO);

    /**
     * 更新广播统计信息
     *
     * @param broadcastId 广播ID
     * @param distributedCount 已分发数量
     * @param receivedCount 已接收数量
     * @param readCount 已读人数
     * @return 是否成功
     */
    boolean updateBroadcastStatistics(String broadcastId, Integer distributedCount,
                                     Integer receivedCount, Integer readCount);

    /**
     * 更新广播状态
     *
     * @param broadcastId 广播ID
     * @param status 目标状态
     * @return 是否成功
     */
    boolean updateBroadcastStatus(String broadcastId, String status);

    /**
     * 批量更新广播状态
     *
     * @param broadcastIds 广播ID列表
     * @param status 目标状态
     * @return 成功更新数量
     */
    int batchUpdateBroadcastStatus(List<String> broadcastIds, String status);

    /**
     * 标记广播为分发中
     *
     * @param broadcastId 广播ID
     * @return 是否成功
     */
    boolean markAsDistributing(String broadcastId);

    /**
     * 标记广播为完成
     *
     * @param broadcastId 广播ID
     * @return 是否成功
     */
    boolean markAsCompleted(String broadcastId);

    /**
     * 处理待分发的广播
     *
     * @param limit 每次处理数量
     * @return 处理的广播数量
     */
    int processPendingDistribute(int limit);

    /**
     * 获取广播统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param broadcastType 广播类型（可选）
     * @return 统计信息VO
     */
    BroadcastStatisticsVO getBroadcastStatistics(LocalDateTime startTime,
                                                LocalDateTime endTime,
                                                String broadcastType);

    /**
     * 根据接收者查询相关广播
     *
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param limit 限制数量
     * @return 广播记录列表
     */
    List<BroadcastDetailVO> getBroadcastsByReceiver(String receiverId,
                                                   String receiverType,
                                                   int limit);

    /**
     * 已读数量加一
     * @param broadcastId
     */
	void incrementReadCount(String broadcastId);

	/**
	 * 修改已分发数量
	 * @param broadcastId
	 * @param distributedCount 已分发数量
	 * @return
	 */
	boolean updateDistributedCount(String broadcastId, Integer distributedCount);

	/**
	 * 修改已接收数量
	 * @param broadcastId
	 * @param receivedCount 已接收数量
	 * @return
	 */
	boolean updateReceivedCount(String broadcastId, Integer receivedCount);

	/**
	 * 修改已读人数
	 * @param broadcastId
	 * @param readCount 已读人数
	 * @return
	 */
	boolean updateReadCount(String broadcastId, Integer readCount);
}