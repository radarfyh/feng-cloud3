package ltd.huntinginfo.feng.agent.service;

import java.util.Map;

import ltd.huntinginfo.feng.agent.api.vo.MsgAppCredentialVO;

public interface MessagePollingService {

	void init();

	/**
	 * 启动轮询服务
	 */
	void start();

	/**
	 * 停止轮询服务
	 */
	void stop();

	/**
	 * 手动触发轮询
	 */
	boolean triggerPolling(String appKey);

	/**
	 * 重置游标
	 */
	boolean resetCursor(String appKey);

	/**
	 * 清理资源
	 */
	void destroy();

	/**
	 * 获取轮询统计信息
	 */
	Map<String, Object> getPollingStatistics();

	/**
	 * 重启轮询服务
	 */
	void restart();

}
