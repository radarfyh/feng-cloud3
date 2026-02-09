package ltd.huntinginfo.feng.center.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.center.api.entity.MsgAgentLog;

import java.util.List;

/**
 * 消息日志表 服务接口
 */
public interface MsgAgentLogService extends IService<MsgAgentLog> {

    /**
     * 根据ID查询日志详情
     */
    MsgAgentLog getById(String id);

    /**
     * 分页查询日志列表
     */
    IPage<MsgAgentLog> page(IPage<MsgAgentLog> page, MsgAgentLog msgAgentLog);

    /**
     * 查询日志列表
     */
    List<MsgAgentLog> list(MsgAgentLog msgAgentLog);

    /**
     * 新增日志
     */
    boolean save(MsgAgentLog msgAgentLog);

    /**
     * 更新日志
     */
    boolean updateById(MsgAgentLog msgAgentLog);

    /**
     * 删除日志
     */
    boolean removeById(String id);

	/**
	 * 记录发送日志
	 */
	void logSend(String msgId, String appKey, String operation, String content, Object detail);

	/**
	 * 记录回调日志
	 */
	void logCallback(String msgId, String appKey, String operation, String content, String apiUrl,
			String httpMethod, Integer httpStatus, Integer responseTime, Object detail);

	/**
	 * 记录错误日志
	 */
	void logError(String msgId, String appKey, String operation, String content, Object detail);

	/**
	 * 记录轮询日志
	 */
	void logPoll(String msgId, String appKey, String operation, String content, Object detail);

	/**
	 * 记录状态更新日志
	 */
	void logStatus(String msgId, String appKey, String operation, String oldStatus, String newStatus,
			String content);
}