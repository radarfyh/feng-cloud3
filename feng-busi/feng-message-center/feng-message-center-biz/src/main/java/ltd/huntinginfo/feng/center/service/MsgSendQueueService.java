package ltd.huntinginfo.feng.center.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.center.api.entity.MsgSendQueue;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 消息发送队列表 服务接口
 */
public interface MsgSendQueueService extends IService<MsgSendQueue> {

    /**
     * 根据ID查询队列详情
     */
    MsgSendQueue getById(String id);

    /**
     * 分页查询队列列表
     */
    IPage<MsgSendQueue> page(IPage<MsgSendQueue> page, MsgSendQueue msgSendQueue);

    /**
     * 查询队列列表
     */
    List<MsgSendQueue> list(MsgSendQueue msgSendQueue);

    /**
     * 新增队列
     */
    boolean save(MsgSendQueue msgSendQueue);

    /**
     * 更新队列
     */
    boolean updateById(MsgSendQueue msgSendQueue);

    /**
     * 删除队列
     */
    boolean removeById(String id);

	/**
	 * 创建回调任务
	 */
	MsgSendQueue createCallbackTask(String appKey, String msgId, Date executeTime);

	void init();

	/**
	 * 启动队列处理
	 */
	void start();

	/**
	 * 停止队列处理
	 */
	void stop();

	/**
	 * 重启队列服务
	 */
	void restart();

	/**
	 * 获取待处理的任务列表
	 */
	List<MsgSendQueue> getPendingTasks(int limit);

	/**
	 * 标记任务为处理中
	 */
	boolean markAsProcessing(String id);

	/**
	 * 标记任务为成功
	 */
	boolean markAsSuccess(String id, String resultCode, String resultMessage);

	/**
	 * 标记任务为失败
	 */
	boolean markAsFailed(String id, String resultCode, String resultMessage);

	/**
	 * 创建发送任务
	 */
	MsgSendQueue createSendTask(String appKey, String msgId, Date executeTime, int priority);

	/**
	 * 创建重试任务
	 */
	MsgSendQueue createRetryTask(String appKey, String msgId, Date executeTime, int currentRetry);

	/**
	 * 获取队列统计信息
	 */
	Map<String, Object> getQueueStatistics();

	/**
	 * 统计各种状态的任务数量
	 */
	long countByStatus(String queueStatus);

	/**
	 * 清理资源
	 */
	void destroy();
}